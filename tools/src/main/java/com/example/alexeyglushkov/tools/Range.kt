/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.alexeyglushkov.tools

/**
 * Immutable class for describing the range of two numeric values.
 *
 *
 * A range (or "interval") defines the inclusive boundaries around a contiguous span of
 * values of some [Comparable] type; for example,
 * "integers from 1 to 100 inclusive."
 *
 *
 *
 * All ranges are bounded, and the left side of the range is always `>=`
 * the right side of the range.
 *
 *
 *
 * Although the implementation itself is immutable, there is no restriction that objects
 * stored must also be immutable. If mutable objects are stored here, then the range
 * effectively becomes mutable.
 */
class Range<T : Comparable<T>?>(lower: T, upper: T) {

    /**
     * Checks if the `value` is within the bounds of this range.
     *
     *
     * A value is considered to be within this range if it's `>=`
     * the lower endpoint *and* `<=` the upper endpoint (using the [Comparable]
     * interface.)
     *
     * @param value a non-`null` `T` reference
     * @return `true` if the value is within this inclusive range, `false` otherwise
     *
     * @throws NullPointerException if `value` was `null`
     */
    operator fun contains(value: T): Boolean {
        checkNotNull(value, "value must not be null")
        val gteLower = value!!.compareTo(lower) >= 0
        val lteUpper = value.compareTo(upper) <= 0
        return gteLower && lteUpper
    }

    /**
     * Checks if another `range` is within the bounds of this range.
     *
     *
     * A range is considered to be within this range if both of its endpoints
     * are within this range.
     *
     * @param range a non-`null` `T` reference
     * @return `true` if the range is within this inclusive range, `false` otherwise
     *
     * @throws NullPointerException if `range` was `null`
     */
    operator fun contains(range: Range<T>): Boolean {
        checkNotNull(range, "value must not be null")
        val gteLower = range.lower!!.compareTo(lower) >= 0
        val lteUpper = range.upper!!.compareTo(upper) <= 0
        return gteLower && lteUpper
    }

    /**
     * Compare two ranges for equality.
     *
     *
     * A range is considered equal if and only if both the lower and upper endpoints
     * are also equal.
     *
     * @return `true` if the ranges are equal, `false` otherwise
     */
    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        } else if (this === obj) {
            return true
        } else if (obj is Range<*>) {
            val other = obj
            return lower == other.lower && upper == other.upper
        }
        return false
    }

    /**
     * Clamps `value` to this range.
     *
     *
     * If the value is within this range, it is returned.  Otherwise, if it
     * is `<` than the lower endpoint, the lower endpoint is returned,
     * else the upper endpoint is returned. Comparisons are performed using the
     * [Comparable] interface.
     *
     * @param value a non-`null` `T` reference
     * @return `value` clamped to this range.
     */
    fun clamp(value: T): T {
        checkNotNull(value, "value must not be null")
        return if (value!!.compareTo(lower) < 0) {
            lower
        } else if (value.compareTo(upper) > 0) {
            upper
        } else {
            value
        }
    }

    /**
     * Returns the intersection of this range and another `range`.
     *
     *
     * E.g. if a `<` b `<` c `<` d, the
     * intersection of [a, c] and [b, d] ranges is [b, c].
     * As the endpoints are object references, there is no guarantee
     * which specific endpoint reference is used from the input ranges:
     *
     *
     * E.g. if a `==` a' `<` b `<` c, the
     * intersection of [a, b] and [a', c] ranges could be either
     * [a, b] or ['a, b], where [a, b] could be either the exact
     * input range, or a newly created range with the same endpoints.
     *
     * @param range a non-`null` `Range<T>` reference
     * @return the intersection of this range and the other range.
     *
     * @throws NullPointerException if `range` was `null`
     * @throws IllegalArgumentException if the ranges are disjoint.
     */
    fun intersect(range: Range<T>): Range<T> {
        checkNotNull(range, "range must not be null")
        val cmpLower = range.lower!!.compareTo(lower)
        val cmpUpper = range.upper!!.compareTo(upper)
        return if (cmpLower <= 0 && cmpUpper >= 0) { // range includes this
            this
        } else if (cmpLower >= 0 && cmpUpper <= 0) { // this inludes range
            range
        } else {
            create(
                    if (cmpLower <= 0) lower else range.lower,
                    if (cmpUpper >= 0) upper else range.upper)
        }
    }

    /**
     * Returns the intersection of this range and the inclusive range
     * specified by `[lower, upper]`.
     *
     *
     * See [.intersect] for more details.
     *
     * @param lower a non-`null` `T` reference
     * @param upper a non-`null` `T` reference
     * @return the intersection of this range and the other range
     *
     * @throws NullPointerException if `lower` or `upper` was `null`
     * @throws IllegalArgumentException if the ranges are disjoint.
     */
    fun intersect(lower: T, upper: T): Range<T> {
        checkNotNull(lower, "lower must not be null")
        checkNotNull(upper, "upper must not be null")
        val cmpLower = lower!!.compareTo(this.lower)
        val cmpUpper = upper!!.compareTo(this.upper)
        return if (cmpLower <= 0 && cmpUpper >= 0) { // [lower, upper] includes this
            this
        } else {
            create(
                    if (cmpLower <= 0) this.lower else lower,
                    if (cmpUpper >= 0) this.upper else upper)
        }
    }

    /**
     * Returns the smallest range that includes this range and
     * another `range`.
     *
     *
     * E.g. if a `<` b `<` c `<` d, the
     * extension of [a, c] and [b, d] ranges is [a, d].
     * As the endpoints are object references, there is no guarantee
     * which specific endpoint reference is used from the input ranges:
     *
     *
     * E.g. if a `==` a' `<` b `<` c, the
     * extension of [a, b] and [a', c] ranges could be either
     * [a, c] or ['a, c], where ['a, c] could be either the exact
     * input range, or a newly created range with the same endpoints.
     *
     * @param range a non-`null` `Range<T>` reference
     * @return the extension of this range and the other range.
     *
     * @throws NullPointerException if `range` was `null`
     */
    fun extend(range: Range<T>): Range<T> {
        checkNotNull(range, "range must not be null")
        val cmpLower = range.lower!!.compareTo(lower)
        val cmpUpper = range.upper!!.compareTo(upper)
        return if (cmpLower <= 0 && cmpUpper >= 0) { // other includes this
            range
        } else if (cmpLower >= 0 && cmpUpper <= 0) { // this inludes other
            this
        } else {
            create(
                    if (cmpLower >= 0) lower else range.lower,
                    if (cmpUpper <= 0) upper else range.upper)
        }
    }

    /**
     * Returns the smallest range that includes this range and
     * the inclusive range specified by `[lower, upper]`.
     *
     *
     * See [.extend] for more details.
     *
     * @param lower a non-`null` `T` reference
     * @param upper a non-`null` `T` reference
     * @return the extension of this range and the other range.
     *
     * @throws NullPointerException if `lower` or `upper` was `null`
     */
    fun extend(lower: T, upper: T): Range<T> {
        checkNotNull(lower, "lower must not be null")
        checkNotNull(upper, "upper must not be null")
        val cmpLower = lower!!.compareTo(this.lower)
        val cmpUpper = upper!!.compareTo(this.upper)
        return if (cmpLower >= 0 && cmpUpper <= 0) { // this inludes other
            this
        } else {
            create(
                    if (cmpLower >= 0) this.lower else lower,
                    if (cmpUpper <= 0) this.upper else upper)
        }
    }

    /**
     * Returns the smallest range that includes this range and
     * the `value`.
     *
     *
     * See [.extend] for more details, as this method is
     * equivalent to `extend(Range.create(value, value))`.
     *
     * @param value a non-`null` `T` reference
     * @return the extension of this range and the value.
     *
     * @throws NullPointerException if `value` was `null`
     */
    fun extend(value: T): Range<T> {
        checkNotNull(value, "value must not be null")
        return extend(value, value)
    }

    /**
     * Return the range as a string representation `"[lower, upper]"`.
     *
     * @return string representation of the range
     */
    override fun toString(): String {
        return String.format("[%s, %s]", lower, upper)
    }

    /**
     * {@inheritDoc}
     */
    override fun hashCode(): Int {
        return hashCodeGeneric(lower, upper)
    }

    /**
     * Get the lower endpoint.
     *
     * @return a non-`null` `T` reference
     */
    val lower: T
    /**
     * Get the upper endpoint.
     *
     * @return a non-`null` `T` reference
     */
    val upper: T

    companion object {
        fun <T> checkNotNull(obj: T?, message: String): T {
            if (obj == null) {
                throw NullPointerException(message)
            }
            return obj
        }

        /**
         * Create a new immutable range, with the argument types inferred.
         *
         *
         *
         * The endpoints are `[lower, upper]`; that
         * is the range is bounded. `lower` must be [lesser or equal][Comparable.compareTo]
         * to `upper`.
         *
         *
         * @param lower The lower endpoint (inclusive)
         * @param upper The upper endpoint (inclusive)
         *
         * @throws NullPointerException if `lower` or `upper` is `null`
         */
        fun <T : Comparable<T>?> create(lower: T, upper: T): Range<T> {
            return Range(lower, upper)
        }

        fun <T> hashCodeGeneric(vararg array: T): Int {
            if (array == null) {
                return 0
            }
            var h = 1
            for (o in array) {
                h = (h shl 5) - h xor (o?.hashCode() ?: 0)
            }
            return h
        }
    }

    /**
     * Create a new immutable range.
     *
     *
     *
     * The endpoints are `[lower, upper]`; that
     * is the range is bounded. `lower` must be [lesser or equal][Comparable.compareTo]
     * to `upper`.
     *
     *
     * @param lower The lower endpoint (inclusive)
     * @param upper The upper endpoint (inclusive)
     *
     * @throws NullPointerException if `lower` or `upper` is `null`
     */
    init {
        this.lower = checkNotNull(lower, "lower must not be null")
        this.upper = checkNotNull(upper, "upper must not be null")
        require(lower!!.compareTo(upper) <= 0) { "lower must be less than or equal to upper" }
    }
}