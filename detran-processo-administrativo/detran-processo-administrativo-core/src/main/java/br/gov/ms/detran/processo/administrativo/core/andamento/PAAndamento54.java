package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.EditalProcessoAdministrativoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.RequisitoRecursoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EditalWrapper;
import javax.persistence.EntityManager;

public class PAAndamento54 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento54.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em,
                                                             ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 54.");
        
        RetornoExecucaoAndamentoWrapper retornoWrapper;

        new EditalProcessoAdministrativoBO().gravaEditalNotificacaoProcessoAdministrativo(em, (EditalWrapper) wrapper.getObjetoWrapper());

        if(wrapper.getProcessoAdministrativo().isPontuacao()){
            
            retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                                 PAFluxoProcessoConstante.FLUXO_DESENTRANHAMENTO_CODIGO,
                                                                 PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH);
        }else{
            retornoWrapper = defineRetornoProcessoJuridico(em, wrapper.getProcessoAdministrativo());
        }
        

        return retornoWrapper;

    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        EditalWrapper entrada = (EditalWrapper) wrapper.getObjetoWrapper();

        new EditalProcessoAdministrativoBO().
                validaEntradaParaEditalNotificacaoProcessoAdministrativo(entrada);

        if (wrapper.getIdUsuario() == null) {
            DetranWebUtils.applicationMessageException("Usuário não informado.");
        }

        if (!TipoFasePaEnum.DESENTRANHAMENTO.equals(entrada.getTipo())) {
            DetranWebUtils.applicationMessageException("Não é possível gravar Edital no andamento Atual do PA.");
        }
    }

    private RetornoExecucaoAndamentoWrapper defineRetornoProcessoJuridico(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        RetornoExecucaoAndamentoWrapper retornoWrapper;
        
        DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, processoAdministrativo.getId());
        
        if(pju == null){
            DetranWebUtils.applicationMessageException("Não foi possível recuperar os dados judiciais do PA.");
        }
        if(RequisitoRecursoBloqueioEnum.SEM_CURSO.equals(pju.getRequisitoCursoBloqueio())){
            retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                                 PAFluxoProcessoConstante.FLUXO_GERAL_JURIDICO,
                                                                 PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_PROTOCOLO_DEVOLUCAO_CNH);
        }else{
            retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO,
                                                                 PAFluxoProcessoConstante.CURSO_EXAME);
        }
        return retornoWrapper;
    }
}
