package com.aglushkov.wordteacher

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.aglushkov.wordteacher.databinding.ActivityMainBinding
import com.aglushkov.wordteacher.features.definitions.view.DefinitionsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import opennlp.tools.postag.POSModel
import opennlp.tools.postag.POSTaggerME
import opennlp.tools.tokenize.Tokenizer
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel


class MainActivity : AppCompatActivity() {
    private val testScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                if (DefinitionsFragment::class.java.name == className) {
                    return DefinitionsFragment()
                }

                return super.instantiate(classLoader, className)
            }
        }

        if (supportFragmentManager.findFragmentByTag("definitions") == null) {
            val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, DefinitionsFragment::class.java.name)
            supportFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(binding.fragmentContainer.id, fragment, "definitions")
                    .commitAllowingStateLoss()
        }

        System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
        testScope.launch {
            resources.openRawResource(R.raw.en_token).use { modelIn ->
                val tokenizerModel = TokenizerModel(modelIn)
                val tokenizer: Tokenizer = TokenizerME(tokenizerModel)
                val tokens = tokenizer.tokenize("Of course this is all on the command line. Many people use the models directly in their Java code by creating SentenceDetector and Tokenizer objects and calling their methods as appropriate. The following section will explain how the Tokenizers can be used directly from java. He's great. I like Mike's apartment.");
                Log.d("ttt", "tokenized: " + tokens)

                resources.openRawResource(R.raw.en_pos_maxent).buffered().use {
                    try {
                        val posTaggerModel = POSModel(it)
                        val tagger = POSTaggerME(posTaggerModel)
                        val tags = tagger.tag(tokens);

                        val tokens2 = tokenizer.tokenize("Of course this is all on the command line. Many people use the models directly in their Java code by creating SentenceDetector and Tokenizer objects and calling their methods as appropriate. The following section will explain how the Tokenizers can be used directly from java. He's great. I like Mike's apartment.");
                        val tags2 = tagger.tag(tokens2);
                        Log.d("ttt", "tags: " + tags)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
    }
}
