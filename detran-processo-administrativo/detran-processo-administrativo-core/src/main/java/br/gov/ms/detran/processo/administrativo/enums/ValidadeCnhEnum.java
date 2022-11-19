
package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import java.util.ResourceBundle;

public enum ValidadeCnhEnum {
    
    VALIDA, VENCIDA;
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
        //return ResourceBundle.getBundle("messages-persistencia").getString(key);
    }
}
