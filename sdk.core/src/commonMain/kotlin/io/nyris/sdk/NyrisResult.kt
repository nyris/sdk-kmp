package io.nyris.sdk

sealed class NyrisResult<out T>(internal val value: Any?) {
    internal val isSuccess: Boolean get() = value !is Throwable
    internal val isFailure: Boolean get() = value is Throwable

    fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Throwable -> value
            else -> null
        }

    companion object {

        internal fun <T> successSingle(value: T): SuccessSingle<T> = SuccessSingle(value)

        internal fun <T> failureSingle(e: Throwable): FailSingle = FailSingle(e)

        internal fun successCompletable(): NyrisResultCompletable = SuccessCompletable

        internal fun failureCompletable(e: Throwable): NyrisResultCompletable = FailCompletable(e)
    }
}

sealed class NyrisResultSingle<T>(value: Any?) : NyrisResult<T>(value)
class SuccessSingle<T>(value: Any?) : NyrisResultSingle<T>(value)
class FailSingle(throwable: Throwable?) : NyrisResultSingle<Throwable>(throwable)

sealed class NyrisResultCompletable(value: Any?) : NyrisResult<Any>(value)
object SuccessCompletable : NyrisResultCompletable(Unit)
class FailCompletable(throwable: Throwable?) : NyrisResultCompletable(throwable)

fun <T> NyrisResult<T>.onSuccess(action: (value: T) -> Unit): NyrisResult<T> {
    if (isSuccess) action(value as T)
    return this
}

fun <T> NyrisResult<T>.onFailure(action: (exception: Throwable) -> Unit): NyrisResult<T> {
    exceptionOrNull()?.let { action(it) }
    return this
}