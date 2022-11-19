package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.AndamentoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoOrigemRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPrioridadeFluxoAmparoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAStatusAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.AndamentoProcessoAdministrativoConsulta;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import java.util.Calendar;
import javax.persistence.EntityManager;

/**
 * @author Lillydi
 */
public class AndamentoProcessoAdministrativoManager {
    
    private PAOcorrenciaStatus ocorrenciaAtual;
    
    /**
     * Finalizar o andamento atual.
     * 
     * @param em
     * @param ocorrenciaAtual
     * @throws AppException 
     */
    public void finalizarAndamentoAtual(EntityManager em, PAOcorrenciaStatus ocorrenciaAtual) throws AppException {
        
        PAOcorrenciaStatusRepositorio repo = new PAOcorrenciaStatusRepositorio();
        
        if (!SituacaoOcorrenciaEnum.INICIADO.equals(ocorrenciaAtual.getSituacao())) {
            DetranWebUtils.applicationMessageException("A situação do andamento é diferente de INICIADO.");
        }
        
        ocorrenciaAtual.setSituacao(SituacaoOcorrenciaEnum.FINALIZADO);
        ocorrenciaAtual.setDataTermino(Calendar.getInstance().getTime());
        
        repo.update(em, ocorrenciaAtual);
        
        em.flush();
    }
    
    /**
     * @param em
     * @param ocorrenciaAtual
     * @throws AppException 
     */
    private void iniciarProximoAndamento(EntityManager em, PAOcorrenciaStatus ocorrenciaAtual) throws AppException {
        
        PAOcorrenciaStatusRepositorio repo = new PAOcorrenciaStatusRepositorio();
        
        AndamentoProcessoAdministrativoConsulta proximoAndamento = 
            new AndamentoProcessoAdministrativoRepositorio()
                .getProximoAndamento(em, ocorrenciaAtual.getProcessoAdministrativo());
        
        if (proximoAndamento == null || proximoAndamento.getCodigoAndamentoProcesso() == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar o próximo andamento do PA.");
        }
        
        PAStatusAndamento statusAndamento = 
            new PAStatusAndamentoRepositorio()
                .getPAStatusAndamentoAtivoPorStatusEAndamento(
                    em, 
                    proximoAndamento.getCodigoAndamentoProcesso().intValue()
                );
        
        ocorrenciaAtual.setStatusAndamento(statusAndamento);
        ocorrenciaAtual.setSituacao(SituacaoOcorrenciaEnum.INICIADO);
        ocorrenciaAtual.setDataInicio(Calendar.getInstance().getTime());
        ocorrenciaAtual.setDataTermino(null);
        
        repo.update(em, ocorrenciaAtual);
        
        em.flush();
    }
    
    /**
     * Próximo andamento, fluxo normal do metadados. Sem mudança de fluxo. O andamento atual deve estar finalizado.
     * 
     * @param em
     * @param processo
     * @throws AppException 
     */
    public void proximoAndamentoComAtualFinalizado(EntityManager em, ProcessoAdministrativo processo) throws AppException{
        
        if (ocorrenciaAtual == null){
            ocorrenciaAtual = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, processo.getId());
        }
        
        if (!SituacaoOcorrenciaEnum.FINALIZADO.equals(ocorrenciaAtual.getSituacao())) {
            DetranWebUtils.applicationMessageException("A situação do andamento é diferente de FINALIZADO.");
        }
        
        iniciarProximoAndamento(em, ocorrenciaAtual);
        
        em.flush();
    }

    /**
     * @param em
     * @param processo
     * @param mudancaFluxo
     * @param codigoPAFluxoProcesso
     * @throws AppException 
     */
    public void mudancaDeFluxoComAndamentoAtualFinalizado(EntityManager em, 
                                                          ProcessoAdministrativo processo, 
                                                          Boolean mudancaFluxo, 
                                                          Integer codigoPAFluxoProcesso) throws AppException{
        
        if (ocorrenciaAtual == null) {
            ocorrenciaAtual = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, processo.getId());
        }
        
        if(mudancaFluxo) { 
        
            executaMudancaFluxo(em, processo, codigoPAFluxoProcesso);
        
        } else {
            
            new AndamentoProcessoAdministrativoManager2().proximoAndamento(em, processo.getId(), null);
        }
    }
    
    /**
     * @param em
     * @param processoAdministrativo
     * @param codigoAndamentoDestino
     * @throws AppException 
     */
    public void alterarParaAndamentoEspecifico(
            EntityManager em, ProcessoAdministrativo processoAdministrativo, Integer codigoAndamentoDestino) throws AppException{
        
        PAOcorrenciaStatusRepositorio repo = new PAOcorrenciaStatusRepositorio();
        
        if (ocorrenciaAtual == null){
            ocorrenciaAtual = repo.getPAOcorrenciaStatusAtiva(em, processoAdministrativo.getId());
        }
        
        finalizarAndamentoAtual(em, ocorrenciaAtual);
        
        PAStatusAndamento statusAndamento = 
            new PAStatusAndamentoRepositorio()
                .getPAStatusAndamentoAtivoPorStatusEAndamento(
                    em, 
                    codigoAndamentoDestino
                );
        
        ocorrenciaAtual.setAtivo(AtivoEnum.ATIVO);
        ocorrenciaAtual.setStatusAndamento(statusAndamento);
        ocorrenciaAtual.setSituacao(SituacaoOcorrenciaEnum.INICIADO);
        ocorrenciaAtual.setDataInicio(Calendar.getInstance().getTime());
        ocorrenciaAtual.setDataTermino(null);
        
        repo.update(em, ocorrenciaAtual);
    }
    
    /**
     * @param em
     * @param processoAdministrativo
     * @param codigoFluxoProcessoDestino
     * @param codigoAndamentoDestino
     * @throws AppException 
     */
    public void alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(
            EntityManager em, ProcessoAdministrativo processoAdministrativo, Integer codigoFluxoProcessoDestino, Integer codigoAndamentoDestino) throws AppException{
        
        PAOcorrenciaStatusRepositorio repo = new PAOcorrenciaStatusRepositorio();
        
        if (ocorrenciaAtual == null){
            ocorrenciaAtual = repo.getPAOcorrenciaStatusAtiva(em, processoAdministrativo.getId());
        }
        
        finalizarAndamentoAtual(em, ocorrenciaAtual);
        
        PAStatusAndamento statusAndamento = 
            new PAStatusAndamentoRepositorio()
                .getPAStatusAndamentoAtivoPorStatusEAndamento(
                    em, 
                    codigoAndamentoDestino
                );
        
        PAFluxoProcesso fluxoProcesso = 
            new PAFluxoProcessoRepositorio()
                .getPAFluxoProcessoPorCodigo(
                    em, 
                    codigoFluxoProcessoDestino
                );
        
        ocorrenciaAtual.setAtivo(AtivoEnum.ATIVO);
        ocorrenciaAtual.setStatusAndamento(statusAndamento);
        ocorrenciaAtual.setSituacao(SituacaoOcorrenciaEnum.INICIADO);
        ocorrenciaAtual.setDataInicio(Calendar.getInstance().getTime());
        ocorrenciaAtual.setDataTermino(null);
        ocorrenciaAtual.setFluxoProcesso(fluxoProcesso);
        
        repo.update(em, ocorrenciaAtual);
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @param codigoFluxoProcessoDestino
     * @param codigoAndamentoDestino
     * @param usuarioAlteracao
     * @throws AppException 
     */
    public void alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(
            EntityManager em, ProcessoAdministrativo processoAdministrativo, Integer codigoFluxoProcessoDestino, Integer codigoAndamentoDestino, String usuarioAlteracao) throws AppException{
        
        PAOcorrenciaStatusRepositorio repo = new PAOcorrenciaStatusRepositorio();
        
        if (ocorrenciaAtual == null){
            ocorrenciaAtual = repo.getPAOcorrenciaStatusAtiva(em, processoAdministrativo.getId());
        }
        
        finalizarAndamentoAtual(em, ocorrenciaAtual);
        
        PAStatusAndamento statusAndamento = 
            new PAStatusAndamentoRepositorio()
                .getPAStatusAndamentoAtivoPorStatusEAndamento(
                    em, 
                    codigoAndamentoDestino
                );
        
        PAFluxoProcesso fluxoProcesso = 
            new PAFluxoProcessoRepositorio()
                .getPAFluxoProcessoPorCodigo(
                    em, 
                    codigoFluxoProcessoDestino
                );
        
        ocorrenciaAtual.setAtivo(AtivoEnum.ATIVO);
        ocorrenciaAtual.setStatusAndamento(statusAndamento);
        ocorrenciaAtual.setSituacao(SituacaoOcorrenciaEnum.INICIADO);
        ocorrenciaAtual.setDataInicio(Calendar.getInstance().getTime());
        ocorrenciaAtual.setDataTermino(null);
        ocorrenciaAtual.setFluxoProcesso(fluxoProcesso);
        if(!DetranStringUtil.ehBrancoOuNulo(usuarioAlteracao)){
            ocorrenciaAtual.setUsuarioAlteracao(usuarioAlteracao);
            ocorrenciaAtual.setDefineUsuarioSessao(Boolean.FALSE);
        }
        
        repo.update(em, ocorrenciaAtual);
    }
    
    /**
     * 
     * @param em
     * @param processo
     * @param codigoPAFluxoProcesso
     * @throws AppException 
     */
    private void executaMudancaFluxo(EntityManager em, ProcessoAdministrativo processo, Integer codigoPAFluxoProcesso) throws AppException {
        
        if (codigoPAFluxoProcesso != null) {
            
            PAFluxoAndamento fluxoAndamento
                = new PAFluxoAndamentoRepositorio().getFluxoAndamentoPorPAEFluxoProcesso(em, processo.getId(), codigoPAFluxoProcesso);
            
            if(fluxoAndamento == null){
                DetranWebUtils.applicationMessageException("Não foi possível encontrar fluxo alternativo para andamento atual do PA.");
            }
            
            PAFluxoProcesso fluxoMudanca = fluxoAndamento.getFluxoProcesso();
            
            if(fluxoMudanca == null){
                DetranWebUtils.applicationMessageException("Não foi possível encontrar fluxo alternativo para andamento atual do PA.");
            }
            
            new PAFluxoOrigemRepositorio()
                .validaMudancaFluxoPorOrigemEFluxoProcesso(
                    em, 
                    ocorrenciaAtual.getProcessoAdministrativo().getOrigemApoio(), 
                    fluxoMudanca
                );
            
            PAPrioridadeFluxoAmparo prioridadeFluxoAmparoDestino
                = new PAPrioridadeFluxoAmparoRepositorio()
                    .getPrioridadeFluxoAmparoAtivoPorFluxoProcesso(em, fluxoAndamento.getFluxoProcesso());
            
            if(prioridadeFluxoAmparoDestino == null || prioridadeFluxoAmparoDestino.getId() == null) {
                DetranWebUtils.applicationMessageException("Não foi possível encontrar fluxo alternativo para andamento atual do PA.");
            }
            
            PAFluxoFase fluxoFaseDestino
                = new PAFluxoFaseRepositorio()
                    .getFluxoFasePorPrioridadeFluxoAmparoOrdenadoAscendentePorPrioridade(
                        em, 
                        prioridadeFluxoAmparoDestino
                    );
            
            if(fluxoFaseDestino == null 
                    || fluxoFaseDestino.getId() == null
                    || fluxoFaseDestino.getAndamentoProcesso() == null
                    || fluxoFaseDestino.getAndamentoProcesso().getId() == null) {
                
                DetranWebUtils.applicationMessageException("Não foi possível encontrar fluxo alternativo para andamento atual do PA.");
            }
            
            PAStatusAndamento statusAndamento = 
                new PAStatusAndamentoRepositorio()
                    .getPAStatusAndamentoAtivoPorStatusEAndamento(
                        em, 
                        fluxoFaseDestino.getAndamentoProcesso().getCodigo()
                    );

            if (statusAndamento == null) {
                DetranWebUtils.applicationMessageException("severity.error.criteria.parametroinvalido2");
            }
            
            ocorrenciaAtual.setFluxoProcesso(fluxoMudanca);
            ocorrenciaAtual.setStatusAndamento(statusAndamento);
            ocorrenciaAtual.setSituacao(SituacaoOcorrenciaEnum.INICIADO);
            ocorrenciaAtual.setDataInicio(Calendar.getInstance().getTime());
            ocorrenciaAtual.setDataTermino(null);
            
            new PAOcorrenciaStatusRepositorio().update(em, ocorrenciaAtual);
            
            em.flush();
        }
    }

    /**
     * @param em
     * @param pa
     * @param fluxoFase 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public void alterarParaFluxoEAndamentoEspecificos(EntityManager em, ProcessoAdministrativo pa, PAFluxoFase fluxoFase) throws AppException {
        
        if (ocorrenciaAtual == null){
            ocorrenciaAtual = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, pa.getId());
        }
        
        finalizarAndamentoAtual(em, ocorrenciaAtual);
        
        if (null == fluxoFase.getPrioridadeFluxoAmparo()
                || null == fluxoFase.getPrioridadeFluxoAmparo().getFluxoProcesso()) {
            DetranWebUtils.applicationMessageException("Parâmetros inválidos");
        }
        ocorrenciaAtual.setFluxoProcesso(fluxoFase.getPrioridadeFluxoAmparo().getFluxoProcesso());
        
        PAStatusAndamento statusAndamento = 
                new PAStatusAndamentoRepositorio().
                        getPAStatusAndamentoPorPAAndamentoProcessoAtivo(em, fluxoFase.getAndamentoProcesso().getId());
        
        if (statusAndamento == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.parametroinvalido2");
        }
        ocorrenciaAtual.setStatusAndamento(statusAndamento);
        ocorrenciaAtual.setSituacao(SituacaoOcorrenciaEnum.INICIADO);
        ocorrenciaAtual.setDataInicio(Calendar.getInstance().getTime());
        ocorrenciaAtual.setDataTermino(null);
        
        new PAOcorrenciaStatusRepositorio().update(em, ocorrenciaAtual);
    }
}
