package pl.dev.bkwiatkowski.common.core.validators

import android.util.Patterns

interface ValidatorRule {
  val defaultMessage: String
  fun check(value: String): Boolean
}

sealed interface TextValidatorRule: ValidatorRule {
  data class MinLength(val minLength: Int): TextValidatorRule {
    override val defaultMessage: String = "Tekst musi mieć co najmniej $minLength znaków"
    override fun check(value: String): Boolean {
      return value.length >= minLength
    }
  }

  data class MaxLength(val maxLength: Int): TextValidatorRule {
    override val defaultMessage: String = "Tekst może mieć maksymalnie $maxLength znaków"
    override fun check(value: String): Boolean {
      return value.length <= maxLength
    }
  }

  data object Required: TextValidatorRule {
    override val defaultMessage: String = "To pole jest wymagane"
    override fun check(value: String): Boolean {
      return value.isNotEmpty()
    }
  }

  data object AtLeastOneSpecialCharacters: TextValidatorRule {
    override val defaultMessage: String = "Tekst musi zawierać znak specjalny"
    override fun check(value: String): Boolean {
      val specialCharRegex = Regex("^(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*\$")
      return specialCharRegex.matches(value)
    }
  }

  data object AtLeastOneDigit: TextValidatorRule {
    override val defaultMessage: String = "Tekst musi zawierać cyfrę"
    override fun check(value: String): Boolean {
      val digitRegex = Regex("^(?=.*\\d).*\$")
      return digitRegex.matches(value)
    }
  }

  data object AtLeastOneLowercaseLetter: TextValidatorRule {
    override val defaultMessage: String = "Tekst musi zawierać małą literę"
    override fun check(value: String): Boolean {
      val lowercaseRegex = Regex("^(?=.*[a-z]).*\$")
      return lowercaseRegex.matches(value)
    }
  }

  data object AtLeastOneUppercaseLetter: TextValidatorRule {
    override val defaultMessage: String = "Tekst musi zawierać wielką literę"
    override fun check(value: String): Boolean {
      val uppercaseRegex = Regex("^(?=.*[A-Z]).*\$")
      return uppercaseRegex.matches(value)
    }
  }

  data object EmailPatter : TextValidatorRule {
    override val defaultMessage: String = "Nieprawidłowy format adresu email"
    override fun check(value: String): Boolean {
      return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }
  }

  data class EqualPasswords(val otherPassword: String): TextValidatorRule {
    override val defaultMessage: String = "Hasła nie są takie same"
    override fun check(value: String): Boolean {
      return value == otherPassword
    }
  }

  data object PhonePattern : TextValidatorRule {
    override val defaultMessage: String = "Nieprawidłowy format numeru telefonu. Wymagane: +48 i 9 cyfr"
    override fun check(value: String): Boolean {
      val phoneRegex = Regex("^\\+48\\d{9}$")
      return phoneRegex.matches(value)
    }
  }
}