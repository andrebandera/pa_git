package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.Municipio;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.adm.IMunicipioService;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP08;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP08BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

public class PAAndamento114  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento114.class);

    private IMunicipioService municipioService;

    private IMunicipioService getMunicipioService() {

        if (municipioService == null) {
            municipioService = (IMunicipioService) JNDIUtil.lookup("ejb/MunicipioService");
        }

        return municipioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 114.");
        ProcessoAdministrativoRepositorio repositorio = new ProcessoAdministrativoRepositorio();
        AEMNPP08 aemnpp08 = new AEMNPP08BO().executarIntegracaoAEMNPP08(wrapper.getProcessoAdministrativo().getCpf());


        ProcessoAdministrativo processoAdministrativo = repositorio
                .find(em, ProcessoAdministrativo.class, wrapper.getProcessoAdministrativo().getId());

        processoAdministrativo.setMunicipioCondutor(getMunicipioService().getIdMunicipioAtivoPorCodigo(Integer.parseInt(aemnpp08.getMunicipio())));
        repositorio.update(em, processoAdministrativo);

        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
    }
}