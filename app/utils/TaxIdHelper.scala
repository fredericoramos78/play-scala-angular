package utils


import org.apache.commons.lang3.StringUtils


object TaxIdHelper {
    
    def isTaxIdValid(taxId: Long): Boolean = this.isTaxIdValid(taxId.toString())
    
    def isTaxIdValid(taxId: String): Boolean = {
        var cnpjWithZeros: Option[String] = None
        val asStr: String = taxId match {
            case x if x.length() <= 11 =>
                cnpjWithZeros = Some(StringUtils.leftPad(x, 14, '0'))
                StringUtils.leftPad(x, 11, '0')
            case y if y.length() <= 14 => 
                StringUtils.leftPad(y, 14, '0')
            case other => other
        }
        // validate the typed taxId. If 'cnpjWithZeros' is not NONE also check it.
        _isValidTaxId(asStr) || cnpjWithZeros.map(x => _isValidTaxId(x)).getOrElse(false) 
    }
    
    private[this] def _isValidTaxId(taxId: String): Boolean = {
        if (taxId.length == 11) { // CPF
            // CPFs used for testing
            if ("|00000000000|11111111111|22222222222|33333333333|44444444444|55555555555|66666666666|77777777777|88888888888|99999999999|"
                .indexOf(taxId) > 0) false

            var sum1, sum2 = 0L
            for((eachChar, index) <- taxId.zipWithIndex) {
                if (!eachChar.isDigit) return false
                val value = eachChar.asDigit
                if (index < 9) sum1 += (10 - index) * value
                if (index < 10) sum2 += (11 - index) * value
            }

            def getDV(sum: Long) = { val x = 11 - sum % 11; if (x > 9) 0 else x }
            val isValidDV1 = { val dv1 = getDV(sum1); dv1 == taxId.charAt(9).asDigit }
            val isValidDV2 = { val dv2 = getDV(sum2); dv2 == taxId.charAt(10).asDigit } 

            isValidDV1 && isValidDV2

        } else if (taxId.length == 14) { // CNPJ
            // CNPJs used for testing
            if ("|00000000000000|11111111111111|22222222222222|33333333333333|44444444444444|55555555555555|66666666666666|77777777777777|88888888888888|99999999999999|"
                .indexOf(taxId) > 0) false

            var sum1, sum2 = 0L
            var mult1 = 5
            var mult2 = 6
            for((eachChar, index) <- taxId.zipWithIndex) {
                if (!eachChar.isDigit) return false
                val value = eachChar.asDigit
                if (index < 12) sum1 += mult1 * value
                if (index < 13) sum2 += mult2 * value
                mult1 -= 1
                mult2 -= 1
                if (mult1 < 2) mult1 = 9
                if (mult2 < 2) mult2 = 9
            }

            def getDV(sum: Long) = { val x = 11 - sum % 11; if (x > 9) 0 else x }
            val isValidDV1 = { val dv1 = getDV(sum1); dv1 == taxId.charAt(12).asDigit }
            val isValidDV2 = { val dv2 = getDV(sum2); dv2 == taxId.charAt(13).asDigit } 
            
            isValidDV1 && isValidDV2

        } else false // not a CPF or CPNJ
    }
}