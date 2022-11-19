package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.recursoonline.RecursoOnlineBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.Recurso;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoOnlineCanceladoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;

import javax.persistence.EntityManager;

public class DesistenciaRecursoInstauracaoPenalizacaoBO {

    private static final Logger LOG = Logger.getLogger(DesistenciaRecursoInstauracaoPenalizacaoBO.class);
    
    public RetornoExecucaoAndamentoWrapper definirDesistenteRecursoInstPen(EntityManager em, ProcessoAdministrativo processo) throws AppException{
        
        RetornoExecucaoAndamentoWrapper wrapper = null;
        PAComplemento desistente = new PAComplementoRepositorio().getPAComplementoPorParametroEAtivo(em, processo, PAParametroEnum.DESISTENCIA_REC_INST_PEN);
        
        if(desistente != null){
            wrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
        }

        return  wrapper;
    }

    public void executaDesistenciaRecInstPen(EntityManager em,
                                             ProcessoAdministrativo pa,
                                             DetranUserDetailsWrapper usuarioLogado,
                                             RecursoOnlineCanceladoWrapper recursoOnlineCanceladoWrapper) throws AppException {

        Integer codigoFluxo;

        Recurso recurso = new RecursoRepositorio().getRecursoAtivoPorProcessoAdministrativoESituacao(em, pa.getId(), SituacaoRecursoEnum.EM_ANALISE);

        if (recurso != null) {

            codigoFluxo = PAFluxoProcessoConstante.FLUXO_DESISTENTE_RECURSO_INSTAURACAO_PENALIZACAO_COM_RECURSO;
            new RecursoBO().
                    cancelarRecursoSemValidar(em,
                            usuarioLogado,
                            new RecursoWrapper(recurso,
                                    "Desistir do Recurso.")
                    );

        } else {

            codigoFluxo = PAFluxoProcessoConstante.FLUXO_DESISTENTE_RECURSO_INSTAURACAO_PENALIZACAO;

        }

        new RecursoOnlineBO().cancelarRecursoOnlineEmBackOffice(em, pa, recursoOnlineCanceladoWrapper);

        new PAInicioFluxoBO().gravarInicioFluxo(em, pa, codigoFluxo);
    }
}