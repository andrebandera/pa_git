package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAStatusEnum;
import java.util.List;
import javax.persistence.EntityManager;

public class PAComplementoRepositorio extends AbstractJpaDAORepository<PAComplemento> {
    
    private static final String PACOMPLEMENTO_VALOR_DESISTENCIA_RECURSO_INSTAURACAO_PENALIZACAO = "1";

    /**
     * @param em
     * @param processoAdministrativo
     * @param parametro
     * @return
     * @throws AppException 
     */
    public PAComplemento getPAComplementoPorParametroEAtivo(EntityManager em, 
                                                            ProcessoAdministrativo processoAdministrativo, 
                                                            PAParametroEnum parametro) throws AppException {
        
        if (processoAdministrativo == null || parametro == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<PAComplemento> complementos = 
                super.getListNamedQuery(
                        em,
                        "PAComplemento.getPAComplementoPorParametroEAtivoEProcessoAdministrativo",
                        parametro,
                        AtivoEnum.ATIVO, 
                        processoAdministrativo.getId());
        
        return DetranCollectionUtil.ehNuloOuVazio(complementos)? null : complementos.get(0);
            
    }
    
    /**
     * @param em
     * @param id
     * @return
     * @throws DatabaseException 
     */
    public PAComplemento getComplementoPorPA(EntityManager em, Long id) throws DatabaseException {
        return getNamedQuery(em, "PAComplemento.getComplementoPorPA", id, AtivoEnum.ATIVO);
    }

    /**
     * @param em
     * @param numeroProcesso
     * @param paParametroEnum
     * @return
     * @throws AppException 
     */
    public IBaseEntity getPAComplementoPorNumeroPAEParametroEAtivo(EntityManager em, String numeroProcesso, PAParametroEnum paParametroEnum) throws AppException {
        
        if(numeroProcesso == null || paParametroEnum == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return 
            super.getNamedQuery(
                em, 
                "PAComplemento.getPAComplementoPorNumeroPAEParametroEAtivo", 
                paParametroEnum, 
                AtivoEnum.ATIVO, 
                numeroProcesso
            );
    }

    /**
     * @param em
     * @param cpf
     * @param origem
     * @return
     * @throws DatabaseException 
     */
    public Object getSomaTempoPenalidadeDoCondutor(EntityManager em, String cpf, OrigemEnum origem) throws DatabaseException {
        Object[] params = {
            cpf, 
            PAParametroEnum.TEMPO_PENALIDADE, 
            PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH, 
            AtivoEnum.ATIVO,
            origem
        };
        return getNamedQuery(em, "PAComplemento.getSomaTempoPenalidadeDoCondutor", params);
    }
    
    /**
     * @param em
     * @param cpf
     * @param origem
     * @return
     * @throws DatabaseException 
     */
    public Object getSomaTempoPenalidadeEmPJUDoCondutor(EntityManager em, String cpf, OrigemEnum origem) throws DatabaseException {
        Object[] params = {
            cpf, 
            PAParametroEnum.TEMPO_PENALIDADE, 
            PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH, 
            AtivoEnum.ATIVO,
            origem,
            BooleanEnum.NAO
        };
        return getNamedQuery(em, "PAComplemento.getSomaTempoPenalidadeEmPJUDoCondutor", params);
    }
    
    /**
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public Object getSomaTempoPenalidadeTodosProcessosAtivos(EntityManager em, String cpf) throws DatabaseException {
        
        Object[] params = {
            cpf, 
            PAParametroEnum.TEMPO_PENALIDADE, 
            DetranCollectionUtil.montaLista(PAStatusEnum.SUSPENSO.getCodigo(),
                                            PAStatusEnum.ARQUIVADO.getCodigo(), 
                                            PAStatusEnum.CANCELADO.getCodigo()),
            AtivoEnum.ATIVO
        };
        
        return getNamedQuery(em, "PAComplemento.getSomaTempoPenalidadeTodosProcessosAtivos", params);
    }

    /**
     * @param em
     * @param paId
     * @return
     * @throws DatabaseException 
     */
    public PAComplemento getPAComplementoParaConfirmaCancelamentoRecurso(EntityManager em, Long paId) throws DatabaseException {
        return super.getNamedQuery(em, 
                                   "PAComplemento.getPAComplementoParaConfirmaCancelamentoRecurso", 
                                   PAParametroEnum.DESISTENCIA_REC_INST_PEN, 
                                   AtivoEnum.ATIVO, 
                                   paId,
                                   PACOMPLEMENTO_VALOR_DESISTENCIA_RECURSO_INSTAURACAO_PENALIZACAO);
    }

    public PAComplemento getComplementoDesistenciaPorPA(EntityManager em, Long id) throws DatabaseException {
        
        Object[] params= {
            id,
            DetranCollectionUtil.
                        montaLista(PAParametroEnum.DESISTENCIA_REC_INST_PEN,
                                   PAParametroEnum.DESISTENCIA_15_SGI,
                                   PAParametroEnum.DESISTENCIA_ENTREGA_CNH),
            AtivoEnum.ATIVO
        };
        List<PAComplemento> lista = getListNamedQuery(em, "PAComplemento.getComplementoDesistenciaPorPA", params);
        
        return DetranCollectionUtil.ehNuloOuVazio(lista)? null: lista.get(0);
    }
}