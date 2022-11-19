package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.FuncionarioOperador;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.TemplateProtocolo;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.FormaProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.ResponsavelProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.ace.IAcessoService;
import br.gov.ms.detran.core.iface.servico.hab.IControleCnhService;
import br.gov.ms.detran.processo.administrativo.core.bo.ProtocoloBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.Calendar;
import javax.persistence.EntityManager;

public class PAAndamento185  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento185.class);
    
    private IControleCnhService controleCnhService;
    private IAcessoService acessoService;
    
     public IControleCnhService getControleCnhService() {
        
        if (controleCnhService == null) {
            controleCnhService = (IControleCnhService) JNDIUtil.lookup("ejb/ControleCnhService");
        }
        
        return controleCnhService;
    }
    
     public IAcessoService getAcessoService() {
        
        if (acessoService == null) {
            acessoService = (IAcessoService) JNDIUtil.lookup("ejb/AcessoService");
        }
        
        return acessoService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 185.");
        
        FuncionarioOperador operador = (FuncionarioOperador) getAcessoService().getFuncionarioOperadorByUsuario(wrapper.getIdUsuario());
        
        TemplateProtocolo template = (TemplateProtocolo) getControleCnhService().
                gravarTemplateProtocoloRecurso(operador.getPostoAtendimento(), 
                                               operador.getUsuario(), 
                                               FormaProtocoloEnum.SISTEMA,
                                               TipoSituacaoProtocoloEnum.APRESENTACAO, 
                                               ResponsavelProtocoloEnum.DETRAN,
                                               null, 
                                               Calendar.getInstance().getTime());
        
        new ProtocoloBO()
                .gravar(
                        em,
                        new PAFluxoFaseRepositorio().getFluxoFaseDoProcessoAdministrativo(em, wrapper.getProcessoAdministrativo()),
                        TipoNotificacaoEnum.PROTOCOLO_PA_DESISTENCIA_REC_INST_PEN,
                        wrapper.getProcessoAdministrativo(),
                        template);
        
        PAComplemento complemento = new PAComplemento(wrapper.getProcessoAdministrativo(), PAParametroEnum.DESISTENCIA_REC_INST_PEN, "1");
        new PAComplementoRepositorio().insert(em, complemento);
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    
    }
}