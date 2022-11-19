package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

public enum TipoDocumentoRecursoPAOnlineEnum {

    FOTO_DOCUMENTO_CONFIRMACAO("doc_foto_documento_confirmacao"),
    NOTIFICACAO_PA("notificacao_pa"),
    DOCUMENTO_FORMULARIO_ASSINADO("doc_formulario_asssinado"),
    PROCURACAO("doc_procuracao"),
    CNH("doc_cnh"),
    DOCUMENTO_OPCIONAL("doc_opcional"),
    DOCUMENTO_FORMULARIO("doc_formulario");

    private final String nomeDocumento;

    TipoDocumentoRecursoPAOnlineEnum(String nomeDocumento) {
        this.nomeDocumento = nomeDocumento;
    }

    public String getNomeDocumento() {
        return nomeDocumento;
    }

    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }

}

