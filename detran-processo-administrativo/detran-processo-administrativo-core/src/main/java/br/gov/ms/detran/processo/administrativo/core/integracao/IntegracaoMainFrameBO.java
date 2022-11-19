package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.integracao.comum.servico.DetranIntegracao;

/**
 * @author yanko.campos
 */
public class IntegracaoMainFrameBO extends DetranIntegracao {

    /**
     * 
     * @param params
     * @return
     * @throws AppException 
     */
    public IResultadoIntegracao executarAEBNH011(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEBNH011", null, params);
    }
    
    /**
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP08(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP08", null, params);
    }
    
    /**
     * @param params
     * @return
     * @throws AppException 
     */
    public IResultadoIntegracao executarAEMNPP09(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP09", null, params);
    }

    /**
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP10(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP10", null, params);
    }

    /**
     * 
     * @param params
     * @return
     * @throws AppException 
     */
    public IResultadoIntegracao executarAEMNPP11(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP11", null, params);
    }

    /**
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP12(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP12", null, params);
    }

    /**
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP13(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP13", null, params);
    }

    /**
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP14(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP14", null, params);
    }

    /**
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP15(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP15", null, params);
    }

    /**
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP16(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP16", null, params);
    }
    
    /**
     * 
     * @param params
     * @return
     * @throws AppException 
     */
    public IResultadoIntegracao executarAEMNPP17(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP17", null, params);
    }
    
    /**
     * @param params
     * @return
     * @throws AppException 
     */
    public IResultadoIntegracao executarAEMNPP18(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP18", null, params);
    }

    /**
     * 
     * @param params
     * @return
     * @throws AppException 
     */
    public IResultadoIntegracao executarAEMNPP25(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP25", null, params);
    }

    /**
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP88(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP88", null, params);
    }

    /**
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP89(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP89", null, params);
    }

    /**
     * Obter informações do cadastro de Infrações para infrações associadas ao
     * um PA.
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP90(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP90", null, params);
    }

    /**
     * Obter detalhes de Autuação da Infração (CON-CAI).
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP92(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP92", null, params);
    }

    /**
     * Obter informações do cadastro de Condutores.
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP93(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP93", null, params);
    }

    /**
     * Obter todas as infrações Ativas (independente da Data) para a Lecom.
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP94(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP94", null, params);
    }

    /**
     * Obter histórico de Pontos (inclusive Suspensos) para a Lecom.
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP95(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP95", null, params);
    }

    /**
     * Obter informações recursos de infrações.
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP96(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP96", null, params);
    }

    /**
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP97(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP97", null, params);
    }

    /**
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP98(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP98", null, params);
    }

    /**
     * Obter detalhes do Renavam-Controle (transações Renainf) de PA instaurado.
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP99(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP99", null, params);
    }
    
    /**
     * Consulta de Bloqueios na Base Local e na Base Nacional.
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP22(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP22", null, params);
    }
    
    /**
     * Incluir, Alterar e Excluir Bloqueios na Base Local e na Base Nacional.
     *
     * @param params
     * @return
     * @throws AppException
     */
    public IResultadoIntegracao executarAEMNPP21(ParametroEnvioIntegracao params) throws AppException {
        return executarIntegracao("AEMNPP21", null, params);
    }
}