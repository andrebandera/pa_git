package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;

public enum PassoRecursoOnlinePAEnum {

    PASSO_01(1, "Identificação CPF | Número Processo | Recurso | E-mail"),
    PASSO_02(2, "Token ou Protocolo"),
    PASSO_03(3, "Dados Condutor"),
    PASSO_04(4, "Documentos | Descrição Motivo Recurso"),
    PASSO_FINAL(5, "Finalizar")
    ;

    private final Integer codigo;
    private final String descricaoPasso;

    private PassoRecursoOnlinePAEnum(Integer codigo, String descricaoPasso) {

        this.codigo         = codigo;
        this.descricaoPasso = descricaoPasso;
    }

    /**
     *
     * @param ordinal
     * @return
     * @throws AppException
     */
    public static PassoRecursoOnlinePAEnum getPassoPorOrdinal(Integer ordinal) throws AppException {

        if(ordinal == null) {
            DetranWebUtils.applicationMessageException("Ordinal inválido.");
        }

        for (PassoRecursoOnlinePAEnum passo : values()) {

            if(passo.ordinal() == ordinal) {
                return passo;
            }
        }

        throw new AppException("Ordinal inválido.");
    }

    public String getDescricaoPasso() {
        return descricaoPasso;
    }

    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }

}
