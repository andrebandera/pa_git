package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

/**
 * @author yanko.campos
 */
public class PAAndamento118 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento118.class);

    private IPAControleFalhaService falhaService;

    public IPAControleFalhaService getFalhaService() {

        if (falhaService == null) {
            falhaService = (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }

        return falhaService;
    }
    
    /**
     * @param em
     * @param wrapper
     * @return
     * @throws AppException 
     */
    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 118.");
        
        RetornoExecucaoAndamentoWrapper retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
        
        if (OrigemEnum.JURIDICA.equals(wrapper.getProcessoAdministrativo().getOrigem())) {
            
            retorno = defineAndamentoProcessoJuridico(em, wrapper, retorno);
            
        }
 
        return retorno;

    }

    private RetornoExecucaoAndamentoWrapper defineAndamentoProcessoJuridico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper, RetornoExecucaoAndamentoWrapper retorno) throws DatabaseException, AppException {
        DadoProcessoJudicial dadoProcessoJudicial =
                new DadoProcessoJudicialRepositorio()
                        .getDadoProcessoJudicialPorPA(
                                em,
                                    wrapper.getProcessoAdministrativo().getId());
        if (null == dadoProcessoJudicial) {
            DetranWebUtils.applicationMessageException("Erro ao recuperar dados do processo judicial.");
        }
        if (null != dadoProcessoJudicial.getIdentificacaoRecolhimentoCnh()) switch (dadoProcessoJudicial.getIdentificacaoRecolhimentoCnh()) {
            case INEXISTENTE:
                retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
                break;

            case DETRAN:
                retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO, PAFluxoProcessoConstante.ENTREGA_CNH);
                break;

            case CARTORIO_JUDICIARIO:
                retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO, PAFluxoProcessoConstante.FLUXO_CARTORIO_JUDICIARIO);
                break;

            default:
                break;
        }
        return retorno;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
    }
}