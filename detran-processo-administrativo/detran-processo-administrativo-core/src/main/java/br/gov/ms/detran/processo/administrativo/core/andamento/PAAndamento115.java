package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoInfracaoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracaoSituacaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * @author yanko.campos
 */
public class PAAndamento115 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento115.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 115.");
        
        ProcessoAdministrativoInfracaoRepositorio paiRepo = new ProcessoAdministrativoInfracaoRepositorio();
        
        List<ProcessoAdministrativoInfracao> infracoes
            = new ProcessoAdministrativoInfracaoRepositorio().getInfracoesPorProcessoParaAEMNPP89(em, wrapper.getProcessoAdministrativo());
        
        for (ProcessoAdministrativoInfracao infracao : infracoes) {

            infracao.setSituacao(ProcessoAdministrativoInfracaoSituacaoEnum.PENDENTE);
            infracao =  paiRepo.update(em, infracao);

            new ProcessoAdministrativoInfracaoBO().sinalizaInfracaoUtilizada(infracao);

            infracao.setSituacao(ProcessoAdministrativoInfracaoSituacaoEnum.CONFIRMADO);
            infracao = (ProcessoAdministrativoInfracao) paiRepo.update(em, infracao);
        }
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);

    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }
}
