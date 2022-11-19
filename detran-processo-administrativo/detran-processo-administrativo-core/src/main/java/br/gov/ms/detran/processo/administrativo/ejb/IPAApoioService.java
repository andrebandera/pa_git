/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatus;
import br.gov.ms.detran.processo.administrativo.wrapper.PAAndamentoProcessoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoProcessoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.PAPrioridadeFluxoAmparoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.PAStatusWrapper;
import java.util.List;

/**
 *
 * @author Carlos Eduardo
 */
public interface IPAApoioService {

    PAStatusWrapper gravarStatusAndamento(PAStatusWrapper wrapper) throws AppException;
    
    PAAndamentoProcessoWrapper gravarAndamentoProcesso(PAAndamentoProcessoWrapper wrapper) throws AppException;
    
    public PAFluxoProcessoWrapper gravarFluxoProcesso(PAFluxoProcessoWrapper wrapper) throws AppException;

    public PAFluxoProcesso getPAFluxoProcessoById(Long id) throws AppException;

    public ApoioOrigemInstauracao getApoioOrigemInstauracaoById(Long id) throws AppException;

    public List getOrigemInstauracaoPorFluxoProcesso(Object criteria) throws AppException;

    public Long getCountOrigemInstauracaoPorFluxoProcesso(Object criteria) throws AppException;

    public PAStatus getPAStatusAtivoPorId(Long id) throws AppException;

    public Long getCountStatusPorAndamentoPA(Object criteria) throws AppException;

    public List getStatusPorAndamentoPA(Object criteria) throws AppException;

    public Long getCountFluxoPorFaseProcesso(Object criteria) throws AppException;

    public List getFluxoPorFaseProcesso(Object criteria) throws AppException;

    public Object getUltimaPrioridadeDoFluxoDaFaseProcesso(Long faseProcessoAdmId) throws DatabaseException;

    public Integer recuperarUltimoValorPrioridadePorPerfil(Long idPerfil, Object entity) throws DatabaseException;

    public PAPrioridadeFluxoAmparo getPAPrioridadeFluxoAmparoAtivoPorPAFaseProcessoAdm(Long id) throws DatabaseException;

    public PAPrioridadeFluxoAmparo getPAPrioridadeFluxoAmparoPorId(Long id) throws DatabaseException;

   //public void removerPAPrioridadeFluxoAmparo(PAPrioridadeFluxoAmparo entidade) throws DatabaseException;

    public void trocarPrioridadesPAPrioridadeFluxoAmparo(Object entity, Integer novaOrdem) throws DatabaseException;

    public void removerPorPerfil(Object entidade) throws DatabaseException;

    public void salvarPrioridadeFluxoAmparo(Object entity) throws DatabaseException, AppException;

    public Long getCountAndamentoPorPrioridadeFluxoAmparo(Object criteria) throws AppException;

    public PAFluxoFase getPAFluxoFasePorId(Long id) throws DatabaseException;

    public List getAndamentoPorPrioridadeFluxoAmparo(Object criteria) throws AppException;

    public void salvarFluxoFase(Object entity) throws DatabaseException, AppException;

    public void trocarPrioridadesFluxoFase(Object entity, Integer novaOrdem) throws DatabaseException;
    
    public List getAndamentoPorDescricao(Object criteria) throws DatabaseException;
    
    public Long getCountAndamentoPorDescricao(Object criteria) throws DatabaseException;
    
    public List getPAFluxoProcessoPorFiltros(Object criteria) throws DatabaseException;
    
    public Long getCountPAFluxoProcessoPorFiltros(Object criteria) throws DatabaseException;
}
