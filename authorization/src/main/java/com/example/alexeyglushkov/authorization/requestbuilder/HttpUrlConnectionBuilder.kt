package com.example.alexeyglushkov.authorization.requestbuilder

import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Represents an HTTP Request object
 *
 * @author Pablo Fernandez
 */
class HttpUrlConnectionBuilder {
    /**
     * Obtains the URL of the HTTP Request.
     *
     * @return the original URL of the HTTP Request
     */
    var stringUrl: String? = null
        private set
    /**
     * Returns the HTTP Verb
     *
     * @return the verb
     */
    var verb: Verb
        private set
    private val querystringParams: ParameterList

    /**
     * Obtains a [ParameterList] of the body parameters.
     *
     * @return a [ParameterList]containing the body parameters.
     */
    val bodyParams: ParameterList
    private val headers: MutableMap<String, String>
    private var payload: String? = null
    private var charset: String? = null
    private var bytePayload: ByteArray? = null
    private var connectionKeepAlive = false
    private var followRedirects = true
    private var connectTimeout: Long? = null
    private var readTimeout: Long? = null

    fun setUrl(url: String?): HttpUrlConnectionBuilder {
        stringUrl = url
        return this
    }

    fun setVerb(verb: Verb): HttpUrlConnectionBuilder {
        this.verb = verb
        return this
    }

    fun build(): HttpURLConnection? {
        val completeUrl = completeUrl
        System.setProperty("http.keepAlive", if (connectionKeepAlive) "true" else "false")
        var connection: HttpURLConnection? = null
        try {
            connection = URL(completeUrl).openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = followRedirects
            connection.requestMethod = verb.name
            if (connectTimeout != null) {
                connection.connectTimeout = connectTimeout!!.toInt()
            }
            if (readTimeout != null) {
                connection.readTimeout = readTimeout!!.toInt()
            }
            addHeaders(connection)
            if (verb == Verb.PUT || verb == Verb.POST) {
                addBody(connection, byteBodyContents)
            }
        } catch (ex: Exception) {
            Log.d(TAG, "HttpUrlConnectionBuilder build exception: " + ex.message)
        }
        return connection
    }

    /**
     * Returns the complete url (host + resource + encoded querystring parameters).
     *
     * @return the complete url.
     */
    val completeUrl: String?
        get() {
            val stringUrl = stringUrl
            if (stringUrl != null) {
                return querystringParams.appendTo(stringUrl)
            } else {
                return null
            }
        }

    internal fun addHeaders(conn: HttpURLConnection?): HttpUrlConnectionBuilder {
        for (key in headers.keys) {
            conn!!.setRequestProperty(key, headers[key])
        }
        return this
    }

    @Throws(IOException::class)
    private fun addBody(conn: HttpURLConnection?, content: ByteArray) {
        conn!!.setRequestProperty(CONTENT_LENGTH, content.size.toString())
        // Set default content type if none is set.
        if (conn.getRequestProperty(CONTENT_TYPE) == null) {
            conn.setRequestProperty(CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
        }
        conn.doOutput = true
        val os = conn.outputStream
        os.write(content)
        os.close()
    }

    /**
     * Add an HTTP Header to the Request
     *
     * @param key the header name
     * @param value the header value
     */
    fun addHeader(key: String, value: String): HttpUrlConnectionBuilder {
        headers[key] = value
        return this
    }

    /**
     * Add a body Parameter (for POST/ PUT Requests)
     *
     * @param key the parameter name
     * @param value the parameter value
     */
    fun addBodyParameter(key: String, value: String): HttpUrlConnectionBuilder {
        bodyParams.add(key, value)
        return this
    }

    /**
     * Add a QueryString parameter
     *
     * @param key the parameter name
     * @param value the parameter value
     */
    fun addQuerystringParameter(key: String, value: String): HttpUrlConnectionBuilder {
        querystringParams.add(key, value)
        return this
    }

    /**
     * Add body payload.
     *
     * This method is used when the HTTP body is not a form-url-encoded string,
     * but another thing. Like for example XML.
     *
     * Note: The contents are not part of the OAuth signature
     *
     * @param payload the body of the request
     */
    fun addPayload(payload: String?): HttpUrlConnectionBuilder {
        this.payload = payload
        return this
    }

    /**
     * Overloaded version for byte arrays
     *
     * @param payload
     */
    fun addPayload(payload: ByteArray): HttpUrlConnectionBuilder {
        bytePayload = payload.clone()
        return this
    }

    /**
     * Get a [ParameterList] with the query string parameters.
     *
     * @return a [ParameterList] containing the query string parameters.
     */
    val queryStringParams: ParameterList?
        get() = try {
            val result = ParameterList()
            val queryString = URL(stringUrl).query
            result.addQuerystring(queryString)
            result.addAll(querystringParams)
            result
        } catch (mue: MalformedURLException) {
            null
        }

    fun getUrl(): URL? {
        return try {
            URL(stringUrl)
        } catch (mue: MalformedURLException) {
            null
        }
    }

    /**
     * Returns the URL without the default port and the query string part.
     *
     * @return the OAuth-sanitized URL
     */
    val sanitizedUrl: String
        get() = if (stringUrl!!.startsWith("http://") && (stringUrl!!.endsWith(":80") || stringUrl!!.contains(":80/"))) {
            stringUrl!!.replace("\\?.*".toRegex(), "").replace(":80".toRegex(), "")
        } else if (stringUrl!!.startsWith("https://") && (stringUrl!!.endsWith(":443") || stringUrl!!.contains(":443/"))) {
            stringUrl!!.replace("\\?.*".toRegex(), "").replace(":443".toRegex(), "")
        } else {
            stringUrl!!.replace("\\?.*".toRegex(), "")
        }//try
    /*catch(UnsupportedEncodingException uee)
{
  return null;
}*/

    /**
     * Returns the body of the request
     *
     * @return form encoded string
     */
    val bodyContents: String
        get() { //try
            run { return String(byteBodyContents, getCharset()) }
            /*catch(UnsupportedEncodingException uee)
        {
          return null;
        }*/
        }

    //try
    /*catch(UnsupportedEncodingException uee)
{
  uee.printStackTrace();
  return null;
}*/
    internal val byteBodyContents: ByteArray
        internal get() {
            val bytePayload = bytePayload
            if (bytePayload != null) return bytePayload
            val body = if (payload != null) payload!! else bodyParams.asFormUrlEncodedString()
            //try
            run { return body.toByteArray(getCharset()) }
            /*catch(UnsupportedEncodingException uee)
        {
          uee.printStackTrace();
          return null;
        }*/
        }

    /**
     * Returns the connection headers as a [Map]
     *
     * @return map of headers
     */
    fun getHeaders(): Map<String, String> {
        return headers
    }

    /**
     * Returns the connection charset. Defaults to [Charset] defaultCharset if not set
     *
     * @return charset
     */
    fun getCharset(): Charset {
        return if (charset == null) Charset.forName("UTF-8") else Charset.forName(charset)
    }

    /**
     * Sets the connect timeout for the underlying [HttpURLConnection]
     *
     * @param duration duration of the timeout
     *
     * @param unit unit of time (milliseconds, seconds, etc)
     */
    fun setConnectTimeout(duration: Int, unit: TimeUnit): HttpUrlConnectionBuilder {
        connectTimeout = unit.toMillis(duration.toLong())
        return this
    }

    /**
     * Sets the read timeout for the underlying [HttpURLConnection]
     *
     * @param duration duration of the timeout
     *
     * @param unit unit of time (milliseconds, seconds, etc)
     */
    fun setReadTimeout(duration: Int, unit: TimeUnit): HttpUrlConnectionBuilder {
        readTimeout = unit.toMillis(duration.toLong())
        return this
    }

    /**
     * Set the charset of the body of the request
     *
     * @param charsetName name of the charset of the request
     */
    fun setCharset(charsetName: String?): HttpUrlConnectionBuilder {
        charset = charsetName
        return this
    }

    /**
     * Sets whether the underlying Http Connection is persistent or not.
     *
     * @see http://download.oracle.com/javase/1.5.0/docs/guide/net/http-keepalive.html
     *
     * @param connectionKeepAlive
     */
    fun setConnectionKeepAlive(connectionKeepAlive: Boolean): HttpUrlConnectionBuilder {
        this.connectionKeepAlive = connectionKeepAlive
        return this
    }

    /**
     * Sets whether the underlying Http Connection follows redirects or not.
     *
     * Defaults to true (follow redirects)
     *
     * @see http://docs.oracle.com/javase/6/docs/api/java/net/HttpURLConnection.html.setInstanceFollowRedirects
     * @param followRedirects
     */
    fun setFollowRedirects(followRedirects: Boolean): HttpUrlConnectionBuilder {
        this.followRedirects = followRedirects
        return this
    }

    override fun toString(): String {
        return String.format("@Request(%s %s)", verb, stringUrl)
    }

    companion object {
        private const val TAG = "HttpUrlCon...Builder"
        private const val CONTENT_LENGTH = "Content-Length"
        private const val CONTENT_TYPE = "Content-Type"
        const val DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded"
    }

    init {
        verb = Verb.GET
        querystringParams = ParameterList()
        bodyParams = ParameterList()
        headers = HashMap()
    }
}