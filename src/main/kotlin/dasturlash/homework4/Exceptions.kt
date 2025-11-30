package dasturlash.homework4

import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.Locale

@RestControllerAdvice
class ExceptionHandler(
    private val messageSource: MessageSource
){
    @ExceptionHandler(ShopApp2Exception::class)
    fun handleShopException(ex: ShopApp2Exception): ResponseEntity<BaseMessage> {
        val locale = LocaleContextHolder.getLocale()
        val message = try {
            messageSource.getMessage(ex.errorType().toString(), null, locale)
        }catch (e: NoSuchMessageException) {
            ex.errorType().toString().replace("_", " ").lowercase()
        }

        return ResponseEntity
            .badRequest()
            .body(BaseMessage(ex.errorType().code, message))
    }
}

sealed class ShopApp2Exception(message: String? = null) : RuntimeException(message){
    abstract fun errorType(): ErrorCode
    protected open fun getErrorMessageArguments(): Array<Any?>? = null
    fun getErrorMessage(errorMessageSource: ResourceBundleMessageSource): BaseMessage {
        return BaseMessage(
            code = errorType().code,
            message = errorMessageSource.getMessage(
                errorType().toString(),
                getErrorMessageArguments() as Array<out Any>?,
                Locale(LocaleContextHolder.getLocale().language)
            )
        )
    }
}

class UserNotFoundException() : ShopApp2Exception(){
    override fun errorType() = ErrorCode.USER_NOT_FOUND
}

class UsernameNotFoundException() : ShopApp2Exception(){
    override fun errorType() = ErrorCode.USERNAME_NOT_FOUND

}

class UserAlreadyExistsException() : ShopApp2Exception(){
    override fun errorType() = ErrorCode.USERNAME_ALREADY_EXISTS
}

class ProductNotFoundException() : ShopApp2Exception(){
    override fun errorType() = ErrorCode.PRODUCT_NOT_FOUND
}

class CategoryNotFoundException() : ShopApp2Exception(){
    override fun errorType() = ErrorCode.CATEGORY_NOT_FOUND

}

class InsufficientFundsException() : ShopApp2Exception(){
    override fun errorType() = ErrorCode.INSUFFICIENT_FUNDS

}