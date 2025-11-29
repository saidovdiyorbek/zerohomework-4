import java.util.Optional

fun <T> Optional<T>.getOrThrowNotFound(myThrow: Throwable): T {
    return this.orElseThrow {
        myThrow
    }
}