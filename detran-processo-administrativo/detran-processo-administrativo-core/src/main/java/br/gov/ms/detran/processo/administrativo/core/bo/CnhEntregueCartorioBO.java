/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.ReflectUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import static br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante.FLUXO_CARTORIO_JUDICIARIO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.enums.IdentificacaoRecolhimentoCnhEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.pju.DadoProcessoJudicialWrapper;
import java.util.Date;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class CnhEntregueCartorioBO {

    public void gravarEntregaCnhEmCartorio(EntityManager em, DadoProcessoJudicialWrapper wrapper) throws AppException {

        if (wrapper == null || DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosAdministrativosParaProcessoJuridico())) {
            DetranWebUtils.applicationMessageException("Não foi possível localizar Processo Judicial para entrega de cnh em cartório.");
        }

        validarGravar(wrapper);
        DadoProcessoJudicialRepositorio pjuRepo = new DadoProcessoJudicialRepositorio();
        Date dataInicioPenalidade = wrapper.getEntidade().getDataInicioPenalidade();
        Date dataEntrega = wrapper.getEntidade().getDataEntrega();

        for (ProcessoAdministrativoWrapper paWrapper : wrapper.getProcessosAdministrativosParaProcessoJuridico()) {
            DadoProcessoJudicial pjuAnterior = pjuRepo.getDadoProcessoJudicialPorPA(em, paWrapper.getId());
            DadoProcessoJudicial pjuAtual = new DadoProcessoJudicial();
            ReflectUtil.copyProperties(pjuAtual, pjuAnterior);

            pjuAnterior.setAtivo(AtivoEnum.DESATIVADO);
            pjuRepo.update(em, pjuAnterior);

            pjuAtual.setId(null);
            pjuAtual.setIdentificacaoRecolhimentoCnh(IdentificacaoRecolhimentoCnhEnum.CARTORIO_JUDICIARIO);
            pjuAtual.setVersaoRegistro(0L);
            pjuAtual.setDataInicioPenalidade(pjuAtual.getIndicativoPrazoIndeterminado().equals(BooleanEnum.SIM) ? dataEntrega : dataInicioPenalidade);
            pjuAtual.setDataEntrega(dataEntrega);
            pjuAtual.setObservacao(wrapper.getEntidade().getObservacao());
            pjuAtual.setDataBloqueio(wrapper.getEntidade().getDataBloqueio());

            pjuRepo.insert(em, pjuAtual);
            
            if(pjuAtual.getIndicativoPrazoIndeterminado().equals(BooleanEnum.NAO))
                new ProcessoRascunhoBloqueioBO().criarRascunhoBloqueioParaJuridico(em, pjuAtual.getProcessoJudicial().getProcessoAdministrativo(), dataInicioPenalidade);

            new PAInicioFluxoBO().gravarInicioFluxo(em, pjuAtual.getProcessoJudicial().getProcessoAdministrativo(), FLUXO_CARTORIO_JUDICIARIO);

            dataInicioPenalidade = Utils.addDayMonth(dataInicioPenalidade, pjuAtual.getPrazoPenalidade() == null ? 0 : pjuAtual.getPrazoPenalidade());

        }

    }

    private void validarGravar(DadoProcessoJudicialWrapper wrapper) throws AppException {
        if (wrapper.getEntidade().getDataBloqueio().after(wrapper.getEntidade().getDataInicioPenalidade())) {
            DetranWebUtils.applicationMessageException("Campo Data Bloqueio deve ser menor ou igual ao campo Data Início Penalidade.");
        }
        if (wrapper.getEntidade().getDataEntrega().after(wrapper.getEntidade().getDataInicioPenalidade())) {
            DetranWebUtils.applicationMessageException("Campo Data Entrega CNH deve ser menor ou igual ao campo Data Início Penalidade.");
        }
    }

}
