package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.core.bo.BloqueioBCABO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoMovimentoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

public class PAAndamento4000 extends ExecucaoAndamento {

    private IPAControleFalhaService falhaService;

    public IPAControleFalhaService getFalhaService() {

        if (falhaService == null) {
            falhaService = (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }

        return falhaService;
    }
    
    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(
        EntityManager em, ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {
        
        TipoRetornoAndamentoEnum retorno = null;
        
        try {
            
            PAComplemento complemento = 
                    new PAComplementoRepositorio()
                            .getPAComplementoPorParametroEAtivo(em, 
                                                                andamentoEspecificoWrapper.getProcessoAdministrativo(),
                                                                PAParametroEnum.TEMPO_PENALIDADE);
            
            BloqueioBCA bloqueio = 
                    new BloqueioBCABO().gravarBloqueioBCA(em, 
                                                          andamentoEspecificoWrapper.getProcessoAdministrativo(), 
                                                          complemento);
            
            new BloqueioBCABO().gravarMovimentoBloqueioBCA(em, 
                                                           bloqueio, 
                                                           andamentoEspecificoWrapper.getIdUsuario(), 
                                                           TipoMovimentoBloqueioBCAEnum.BLOQUEIO);
            
//            new AndamentoProcessoAdministrativoManager2()
//                    .proximoAndamento(em, 
//                                      andamentoEspecificoWrapper.getProcessoAdministrativo().getId(), 
//                                      null);
            
            retorno = TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO;

        } catch (Exception e) {
            
            getFalhaService()
                    .gravarFalhaEspecifica(
                            andamentoEspecificoWrapper.getProcessoAdministrativo().getCpf(),
                            "Falha ao fazer o bloqueio do Condutor na WEB para o PA " + andamentoEspecificoWrapper.getNumeroProcesso(),
                            "PAAndamento43");
            
            throw e;
        }
        
        return null;
    }

    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {
        
    }
}