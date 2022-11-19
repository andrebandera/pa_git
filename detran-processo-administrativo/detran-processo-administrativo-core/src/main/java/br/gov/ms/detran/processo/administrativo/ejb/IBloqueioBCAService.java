/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.iface.bca.IBeanIntegracao;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP22;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.ConsistenciaBloqueioBCAAtualizacao;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.FunctionConsistenciaBloqueioBCAAtualizacao;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.wrapper.ParametrosIntegracaoBloqueioBCAWrapper;
import java.util.List;

/**
 *
 * @author Christiano Carrilho.
 */
public interface IBloqueioBCAService {

    /**
     * 
     * @param processo
     * @param pid
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    void executarAEMNPP11(ProcessoAdministrativo processo, String pid) throws AppException;
    
    /**
     * 
     * @param processo
     * @param pid
     * @param proximoAndamento
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    void executarAEMNPP11(ProcessoAdministrativo processo, String pid, Boolean proximoAndamento) throws AppException;
    
    /**
     * 
     * @param processo
     * @param pid
     * @throws AppException 
     */
    void executarDesbloqueioAEMNPP11(ProcessoAdministrativo processo, String pid, String observacao) throws AppException;

    /**
     * 
     * @param wrapper
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    void executarAEMNPP13(ParametrosIntegracaoBloqueioBCAWrapper wrapper) throws AppException;
    
    /**
     * 
     * @param wrapper
     * @param proximoAndamento
     * @throws AppException 
     */
    void executarAEMNPP13(ParametrosIntegracaoBloqueioBCAWrapper wrapper, Boolean proximoAndamento) throws AppException;
    
    /**
     * 
     * @param wrapper 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    void executarAEMNPP14(ParametrosIntegracaoBloqueioBCAWrapper wrapper) throws AppException;
    
    /**
     * 
     * @param wrapper 
     * @param proximoAndamento 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    void executarAEMNPP14(ParametrosIntegracaoBloqueioBCAWrapper wrapper, Boolean proximoAndamento) throws AppException;
    
    /**
     * 
     * @param processo
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    void executarAEMNPP25(ProcessoAdministrativo processo) throws AppException;
    
    void executarAEMNPP17(DadoProcessoJudicial dadoProcessoJudicial, 
                         BloqueioBCA bloqueio, 
                         PAPenalidadeProcesso penalidade) throws AppException;
    
    /**
     * 
     * @param lProcesso
     * @param aemnpp22
     * @return 
     * @throws AppException 
     */
    public List<ConsistenciaBloqueioBCAAtualizacao> registraConsultaBloqueioLocal(List<FunctionConsistenciaBloqueioBCAAtualizacao> lProcesso, AEMNPP22 aemnpp22) throws AppException;
    
    /**
     * 
     * @param lProcesso
     * @param aemnpp22
     * @return 
     * @throws AppException 
     */
    public List<ConsistenciaBloqueioBCAAtualizacao> registraConsultaBloqueioNacional(List<FunctionConsistenciaBloqueioBCAAtualizacao> lProcesso, AEMNPP22 aemnpp22) throws AppException;
    
    /**
     * 
     * @param lProcesso
     * @param aemnpp22
     * @return
     * @throws AppException 
     */
    public List<ConsistenciaBloqueioBCAAtualizacao> registraConsultaBloqueioWeb(List<FunctionConsistenciaBloqueioBCAAtualizacao> lProcesso, AEMNPP22 aemnpp22) throws AppException;
    
    /**
     * 
     * @param parametroEnvioIntegracao
     * @return
     * @throws AppException 
     */
    public IBeanIntegracao executaGravacaoBloqueioBCA(Object parametroEnvioIntegracao) throws AppException;
}