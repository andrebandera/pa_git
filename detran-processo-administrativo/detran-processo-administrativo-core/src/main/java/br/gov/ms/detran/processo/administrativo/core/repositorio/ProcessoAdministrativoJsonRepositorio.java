package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PASituacaoJsonEnum;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoJson;
import br.gov.ms.detran.processo.administrativo.enums.PAStatusEnum;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;

public class ProcessoAdministrativoJsonRepositorio extends AbstractJpaDAORepository<ProcessoAdministrativoJson> {

    /**
     *
     * @param em
     * @return
     * @throws AppException
     */
    public List<ProcessoAdministrativoJson> getPAsInstauradosDiario(EntityManager em) throws AppException {

        return 
            getListNamedQuery(
                em,
                "ProcessoAdministrativoJson.getPAsInstauradosDiario",
                Calendar.getInstance().getTime(),
                AtivoEnum.ATIVO
            );
    }

    /**
     * 
     * @param em
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativoJson> getPAsInstauradosAguardaFinalizaInstauracao(EntityManager em) throws AppException {

        Object[] params = {
            PAStatusEnum.ATIVO.getCodigo(),
            PAAndamentoProcessoConstante.INSTAURACAO.CONFIRMAR_INSTAURACAO,
            AtivoEnum.ATIVO
        };
        
        return 
            getListNamedQuery(
                em,
                "ProcessoAdministrativoJson.getPAsInstauradosPendentesEnvioBPMS",
                params
            );
    }

    /**
     * @param em
     * @param processoAdministrativoId
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException
     */
    public ProcessoAdministrativoJson getProcessoAdministrativoJsonPorProcessoAdministrativoAtivo(
        EntityManager em, Long processoAdministrativoId) throws DatabaseException {

        return 
            getNamedQuery(
                em, 
                "ProcessoAdministrativoJson.getProcessoAdministrativoJsonPorProcessoAdministrativoAtivo",
                processoAdministrativoId,
                AtivoEnum.ATIVO
            );
    }

    /**
     *
     * @param em
     * @param processoAdministrativoJson
     * @throws AppException
     */
    public void alterarSituacaoProcessoAdministrativoJson(
        EntityManager em, ProcessoAdministrativoJson processoAdministrativoJson) throws AppException {

        if (processoAdministrativoJson == null
                || processoAdministrativoJson.getProcessoAdministrativo() == null
                || DetranStringUtil.ehBrancoOuNulo(processoAdministrativoJson.getProcessoAdministrativo().getCpf())) {

            DetranWebUtils.applicationMessageException("Processo Administrativo inválido.");
        }
        
        processoAdministrativoJson.setDefineUsuarioSessao(Boolean.FALSE);
        processoAdministrativoJson.setUsuarioAlteracao("BPMS");
        processoAdministrativoJson.setSituacao(PASituacaoJsonEnum.ENVIADO);
        
        update(em, processoAdministrativoJson);
    }
}