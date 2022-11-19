package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.IJSPASequencial;
import br.gov.ms.detran.comum.util.adapter.CPFAdapter;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author yanko.campos
 */
@Entity
@Table(name = "TB_TDC_PAD_PROCESSO_ADMINISTRATIVO")
@NamedQueries({
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessosAdministrativosParaProcessoJuridico",
            query = "SELECT tdc FROM ProcessoAdministrativo tdc WHERE tdc.cpf = :p0 "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessoAdministrativoPorId",
            query = "SELECT tdc "
            + " FROM ProcessoAdministrativo tdc "
            + " WHERE tdc.id = :p0 "
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessosAdministrativosAtivosOuSuspensosPorCpfParaAEMNPP06",
            query = "SELECT new ProcessoAdministrativo(pa.id) "
            + "FROM PAOcorrenciaStatus os "
            + "INNER JOIN os.processoAdministrativo pa "
            + "INNER JOIN os.statusAndamento sa "
            + "INNER JOIN sa.status s "
            + "WHERE pa.cpf = :p0 "
            + "AND s.codigo IN (:p1) "
            + "AND pa.ativo = :p2 "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessoAdministrativoPorNumeroProcessoSemAtivo",
            query = "SELECT tdc From ProcessoAdministrativo tdc where tdc.numeroProcesso = :p0"),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamentoIniciadoParaBCA",
            query = "SELECT new br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCAWrapper("
            + " tdc.id, "
            + " tdc.numeroProcesso, "
            + " tdc.cpf, "
            + " tdc.cnh, "
            + " tcb.codigo,"
            + " tdc.tipo, "
            + " tdh.artigoInciso,"
            + " tdh,"
            + " tdc.origem) "
            + " FROM PAOcorrenciaStatus tde "
            + "     INNER JOIN tde.processoAdministrativo tdc "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "     INNER JOIN tcd.andamentoProcesso tcb "
            + "     INNER JOIN tdc.origemApoio tdh "
            + " WHERE tdc.ativo = :p0 "
            + "     AND tde.situacao = :p1  "
            + "     AND tcb.codigo in (:p2) "
            + "     AND tcb.ativo = :p0 "
            + "     AND tde.ativo = :p0 "
            + "     AND tcd.ativo = :p0 "
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamentoETipo",
            query = "SELECT tdc "
            + " FROM PAOcorrenciaStatus tde "
            + "     INNER JOIN tde.processoAdministrativo tdc "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "     INNER JOIN tcd.andamentoProcesso tcb "
            + " WHERE tdc.ativo = :p0 "
            + "     AND tde.situacao = :p1  "
            + "     AND tcb.codigo in (:p2) "
            + "     AND tdc.tipo in (:p3)"
            + "     AND tcb.ativo = :p0 "
            + "     AND tde.ativo = :p0 "
            + "     AND tcd.ativo = :p0 "
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamentoTipoETipoNotificacao",
            query = "SELECT tdc "
            + " FROM PAOcorrenciaStatus tde "
            + "     INNER JOIN tde.processoAdministrativo tdc "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "     INNER JOIN tcd.andamentoProcesso tcb "
            + " WHERE tdc.ativo = :p0 "
            + "     AND tde.situacao = :p1  "
            + "     AND tcb.codigo in (:p2) "
            + "     AND tdc.tipo in (:p3)"
            + "     AND tcb.ativo = :p0 "
            + "     AND tde.ativo = :p0 "
            + "     AND tcd.ativo = :p0 "
            + "     AND EXISTS(FROM NotificacaoProcessoAdministrativo tcx "
            + "         INNER JOIN tcx.processoAdministrativo tdc1 where tdc.id = tdc1.id and tcx.ativo = :p0 and tcx.tipoNotificacaoProcesso = :p4)"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessosJuridicosPorAndamentoTipoERecolhimentoCnh",
            query = "SELECT tdc "
            + " FROM PAOcorrenciaStatus tde "
            + "     INNER JOIN tde.processoAdministrativo tdc "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "     INNER JOIN tcd.andamentoProcesso tcb "
            + " WHERE tdc.ativo = :p0 "
            + "     AND tde.situacao = :p1  "
            + "     AND tcb.codigo in (:p2) "
            + "     AND tdc.tipo in (:p3)"
            + "     AND tcb.ativo = :p0 "
            + "     AND tde.ativo = :p0 "
            + "     AND tcd.ativo = :p0 "
            + "     AND EXISTS(Select 1 FROM DadoProcessoJudicial tek "
            + "     INNER JOIN tek.processoJudicial tej "
            + "     INNER JOIN tej.processoAdministrativo tdc1 where tdc1.id = tdc.id and tek.identificacaoRecolhimentoCnh = :p4 and tek.ativo = :p0 )"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessosJuridicosPorAndamentoTipoESemPenalidade",
            query = "SELECT tdc "
            + " FROM PAOcorrenciaStatus tde "
            + "     INNER JOIN tde.processoAdministrativo tdc "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "     INNER JOIN tcd.andamentoProcesso tcb "
            + " WHERE tdc.ativo = :p0 "
            + "     AND tde.situacao = :p1  "
            + "     AND tcb.codigo = :p2 "
            + "     AND tdc.tipo in (:p3)"
            + "     AND tcb.ativo = :p0 "
            + "     AND tde.ativo = :p0 "
            + "     AND tcd.ativo = :p0 "
            + "     AND EXISTS(Select 1  From BloqueioBCA tdk "
            + "                 INNER JOIN tdk.processoAdministrativo tdc1 where tdc.id = tdc1.id and tdk.ativo = :p0)"
            + "      AND NOT EXISTS(SELECT 1 From PAPenalidadeProcesso tea "
                    + "         INNER JOIN tea.processoAdministrativo tdc1 where tdc.id = tdc1.id and tea.ativo = :p0 )"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessoAdministrativoParaBCAPorCPFeProcessoAdministrativo",
            query = "SELECT new br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCAWrapper("
            + " tdc.id, "
            + " tdc.numeroProcesso, "
            + " tdc.cpf, "
            + " tdc.cnh, "
            + " tcb.codigo,"
            + " tdc.tipo, "
            + " tdh.artigoInciso,"
            + " tdh,"
            + " tdc.origem) "
            + " FROM PAOcorrenciaStatus tde "
            + "     INNER JOIN tde.processoAdministrativo tdc "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "     INNER JOIN tcd.andamentoProcesso tcb "
            + "     INNER JOIN tdc.origemApoio tdh "
            + " WHERE tdc.ativo = :p0 "
            + "     AND tde.situacao = :p1  "
            + "     AND tcb.ativo = :p0 "
            + "     AND tde.ativo = :p0 "
            + "     AND tcd.ativo = :p0 "
            + "     AND tdc.cpf = :p2 "
            + "     AND tdc.id = :p3 "
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessoAdministrativoPorAndamentoEIdProcessoAdministrativo",
            query = "SELECT tdc "
            + " FROM ProcessoAdministrativo tdc "
            + " WHERE tdc.ativo = :p0 "
            + "     AND EXISTS(SELECT 1 "
            + "                 FROM PAOcorrenciaStatus tde "
            + "                     INNER JOIN tde.statusAndamento tcd "
            + "                     INNER JOIN tcd.andamentoProcesso tcb "
            + "                 WHERE tde.processoAdministrativo.id = tdc.id "
            + "                     AND tcb.codigo in (:p1) "
            + "                     AND tcb.ativo = :p0 "
            + "                     AND tde.ativo = :p0 "
            + "                     AND tcd.ativo = :p0"
            + " ) "
            + " AND tdc.id = :p2 "
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamento",
            query = "SELECT tdc "
            + " FROM ProcessoAdministrativo tdc "
            + " WHERE tdc.ativo = :p0 "
            + "     AND EXISTS(SELECT 1 "
            + "                 FROM PAOcorrenciaStatus tde "
            + "                     INNER JOIN tde.statusAndamento tcd "
            + "                     INNER JOIN tcd.andamentoProcesso tcb "
            + "                 WHERE tde.processoAdministrativo.id = tdc.id "
            + "                     AND tcb.codigo in (:p1) "
            + "                     AND tcb.ativo = :p0 "
            + "                     AND tde.ativo = :p0 "
            + "                     AND tcd.ativo = :p0"
            + " ) "
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamentoETipoProcessoEmLote",
            query = "SELECT tdc "
            + " FROM ProcessoAdministrativo tdc "
            + " WHERE tdc.ativo = :p0 "
            + "     AND EXISTS(SELECT 1 "
            + "                 FROM PAOcorrenciaStatus tde "
            + "                     INNER JOIN tde.statusAndamento tcd "
            + "                     INNER JOIN tcd.andamentoProcesso tcb "
            + "                 WHERE tde.processoAdministrativo.id = tdc.id "
            + "                     AND tcb.codigo in (:p1) "
            + "                     AND tcb.ativo = :p0 "
            + "                     AND tde.ativo = :p0 "
            + "                     AND tcd.ativo = :p0) "
            + "     AND (:p2 IS NULL or tdc.tipo = :p2) "
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListProcessoAdministrativoPorCpfSemFaseArquivamentoECancelamento",
            query = "SELECT new ProcessoAdministrativo(tdc.id, tdc.numeroProcesso, tdc.versaoRegistro) "
            + "FROM PAOcorrenciaStatus tde "
            + "INNER JOIN tde.processoAdministrativo tdc "
            + "INNER JOIN tde.statusAndamento tcd "
            + "INNER JOIN tcd.status tca "
            + "INNER JOIN tde.fluxoProcesso tck "
            + "WHERE tdc.cpf = :p0 "
            + "AND EXISTS (SELECT 1 "
            + "             FROM PAFluxoFase tch "
            + "             INNER JOIN tch.prioridadeFluxoAmparo tci "
            + "             INNER JOIN tci.faseProcessoAdm tcv "
            + "             INNER JOIN tci.fluxoProcesso tck2 "
            + "             WHERE tck.id = tck2.id "
            + "             AND (tcv.codigo NOT LIKE :p1 AND tcv.codigo NOT LIKE :p2)"
            + "            )"
            + "AND tdc.ativo = :p3 "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessoAdministrativoPorCpfEAndamento",
            query = "SELECT new ProcessoAdministrativo(tdc.id, tdc.versaoRegistro) "
            + "FROM ProcessoAdministrativo tdc "
            + "WHERE tdc.cpf = :p0 "
            + "AND tdc.ativo = :p1 "
            + "AND EXISTS(SELECT 1 "
            + "FROM PAOcorrenciaStatus tde "
            + "INNER JOIN tde.statusAndamento tcd "
            + "INNER JOIN tcd.andamentoProcesso tcb "
            + "WHERE tde.processoAdministrativo.id = tdc.id "
            + "AND tcb.codigo = :p2 "
            + "AND tcb.ativo = :p1 "
            + "AND tde.ativo = :p1 "
            + "AND tcd.ativo = :p1)"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListProcessoAdministrativosAtivosPorCPFParaAberturaRecurso",
            query = "SELECT pa "
            + "FROM PAOcorrenciaStatus os "
            + "INNER JOIN os.processoAdministrativo pa "
            + "INNER JOIN os.statusAndamento sa "
            + "INNER JOIN sa.status s "
            + "WHERE pa.cpf = :p0 "
            + " AND s.codigo NOT IN (:p1) "
            + " AND pa.ativo = :p2 "
            + " AND NOT EXISTS(SELECT 1 FROM Recurso td) "
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.validaSeExisteProcessoAdministrativoValidoParaExecutarDesbloqueioCnh",
            query = "SELECT new ProcessoAdministrativo(tdc.id) "
            + "FROM ProcessoAdministrativo tdc "
            + "WHERE tdc.numeroDetran = :p0 "
            + " AND tdc.ativo = :p1 "
            + " AND EXISTS(SELECT 1 "
            + "             FROM PAOcorrenciaStatus tde "
            + "                 INNER JOIN tde.statusAndamento tcd "
            + "                 INNER JOIN tcd.andamentoProcesso tcb "
            + "             WHERE tdc.id = tde.processoAdministrativo.id "
            + "                 AND tcb.codigo = :p2 "
            + "                 AND tcb.ativo = :p1 "
            + "                 AND tde.ativo = :p1 "
            + "                 AND tcd.ativo = :p1 ) "
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.existeMaisDeUmProcessoAdministrativoParaCondutor",
            query = "SELECT new ProcessoAdministrativo(tdc.id) "
            + "FROM ProcessoAdministrativo tdc "
            + "WHERE tdc.cpf = :p0 "
            + " AND tdc.ativo = :p2 "
            + " AND EXISTS(SELECT 1 "
            + "             FROM PAOcorrenciaStatus tde "
            + "                 INNER JOIN tde.statusAndamento tcd "
            + "                 INNER JOIN tcd.status tca "
            + "             WHERE tdc.id = tde.processoAdministrativo.id "
            + "                 AND tca.codigo = :p1 "
            + "                 AND tca.ativo = :p2 "
            + "                 AND tde.ativo = :p2 "
            + "                 AND tcd.ativo = :p2 ) "
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.countSearch",
            query = "SELECT count(tdc.id) FROM ProcessoAdministrativo tdc WHERE tdc.execucaoInstauracao.id = :p0"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessoAdministrativoPorNumeroEAndamentoEIniciado",
            query = "SELECT new ProcessoAdministrativo(tdc.id, tdc.versaoRegistro) "
            + " FROM ProcessoAdministrativo tdc "
            + " WHERE tdc.ativo = :p0 "
            + "     AND tdc.numeroProcesso = :p3 "
            + "     AND EXISTS(SELECT 1 "
            + "                 FROM PAOcorrenciaStatus tde "
            + "                     INNER JOIN tde.statusAndamento tcd "
            + "                     INNER JOIN tcd.andamentoProcesso tcb "
            + "                 WHERE tde.processoAdministrativo.id = tdc.id "
            + "                     AND tde.situacao = :p1  "
            + "                     AND tcb.codigo in (:p2) "
            + "                     AND tcb.ativo = :p0 "
            + "                     AND tde.ativo = :p0 "
            + "                     AND tcd.ativo = :p0)"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamentoIniciado",
            query = "SELECT new ProcessoAdministrativo(tdc.id, tdc.numeroProcesso, tdc.numeroDetran, tdc.cpf, tdc.versaoRegistro) "
            + " FROM ProcessoAdministrativo tdc "
            + " WHERE tdc.ativo = :p0 "
            + "     AND EXISTS(SELECT 1 "
            + "                 FROM PAOcorrenciaStatus tde "
            + "                     INNER JOIN tde.statusAndamento tcd "
            + "                     INNER JOIN tcd.andamentoProcesso tcb "
            + "                 WHERE tde.processoAdministrativo.id = tdc.id "
            + "                     AND tde.situacao = :p1  "
            + "                     AND tcb.codigo in (:p2) "
            + "                     AND tcb.ativo = :p0 "
            + "                     AND tde.ativo = :p0 "
            + "                     AND tcd.ativo = :p0)"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessoAdministrativoPorNumeroDetranEAndamentoESituacaoIniciado",
            query = "SELECT new ProcessoAdministrativo(tdc.id, tdc.numeroProcesso, tdc.origem, tdc.versaoRegistro) "
            + " FROM ProcessoAdministrativo tdc "
            + " WHERE tdc.numeroDetran = :p0 "
            + "     AND tdc.ativo = :p1 "
            + "     AND EXISTS(SELECT 1 "
            + "                 FROM PAOcorrenciaStatus tde "
            + "                     INNER JOIN tde.statusAndamento tcd "
            + "                     INNER JOIN tcd.andamentoProcesso tcb "
            + "                 WHERE tde.processoAdministrativo.id = tdc.id "
            + "                     AND tde.situacao = :p2  "
            + "                     AND tcb.codigo = :p3 "
            + "                     AND tcb.ativo = :p1 "
            + "                     AND tde.ativo = :p1 "
            + "                     AND tcd.ativo = :p1)"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getPAControleCnhWrapperPorCpfEAndamentoESituacaoIniciado",
            query = "SELECT "
            + "new br.gov.ms.detran.processo.administrativo.wrapper.PAControleCnhWrapper("
            + "         tdc, "
            + "         (SELECT max(tdj.numeroPortaria) "
            + "                FROM NotificacaoComplemento tdj "
            + "                     INNER JOIN tdj.notificacao tcx "
            + "                where tcx.processoAdministrativo.id = tdc.id "
            + "                     and tcx.ativo = :p1 "
            + "                     and tdj.ativo = :p1 ), "
            + "         (SELECT tdl.valor "
            + "                FROM PAComplemento tdl "
            + "                where tdl.processoAdministrativo.id = tdc.id "
            + "                     and tdl.ativo = :p1 "
            + "                     and tdl.parametro = :p4)"
            + " ) "
            + " FROM ProcessoAdministrativo tdc "
            + " WHERE tdc.cpf = :p0 "
            + "     AND tdc.ativo = :p1 "
            + "     AND tdc.origem = :p5 "
            + "     AND (tdc.origemApoio.regra = :p7 OR EXISTS(SELECT 1 "
            + "                FROM NotificacaoComplemento tdj "
            + "                     INNER JOIN tdj.notificacao tcx "
            + "                where tcx.processoAdministrativo.id = tdc.id "
            + "                     and tcx.tipoNotificacaoProcesso <> :p6"
            + "                     and tcx.ativo = :p1 "
            + "                     and tdj.ativo = :p1)) "
            + "     AND EXISTS(SELECT 1 "
            + "                 FROM PAOcorrenciaStatus tde "
            + "                     INNER JOIN tde.statusAndamento tcd "
            + "                     INNER JOIN tcd.andamentoProcesso tcb "
            + "                 WHERE tde.processoAdministrativo.id = tdc.id "
            + "                     AND tde.situacao = :p2  "
            + "                     AND tcb.codigo = :p3 "
            + "                     AND tcb.ativo = :p1 "
            + "                     AND tde.ativo = :p1 "
            + "                     AND tcd.ativo = :p1)"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getPAControleCnhWrapperPJUPorCpfEAndamentoESituacaoIniciado",
            query = "SELECT "
            + "new br.gov.ms.detran.processo.administrativo.wrapper.PAControleCnhWrapper("
            + "         tdc, "
            + "         (SELECT max(tdj.numeroPortaria) "
            + "                FROM NotificacaoComplemento tdj "
            + "                     INNER JOIN tdj.notificacao tcx "
            + "                where tcx.processoAdministrativo.id = tdc.id "
            + "                     and tcx.ativo = :p1 "
            + "                     and tdj.ativo = :p1 ), "
            + "         (SELECT tdl.valor "
            + "                FROM PAComplemento tdl "
            + "                where tdl.processoAdministrativo.id = tdc.id "
            + "                     and tdl.ativo = :p1 "
            + "                     and tdl.parametro = :p4),"
            + "          tek.indicativoPrazoIndeterminado"
            + ") "
            + " FROM DadoProcessoJudicial tek "
            + "     INNER JOIN tek.processoJudicial tej "
            + "     INNER JOIN tej.processoAdministrativo tdc "
            + " WHERE tdc.cpf = :p0 "
            + "     AND tdc.ativo = :p1 "
            + "     AND EXISTS(SELECT 1 "
            + "                 FROM PAOcorrenciaStatus tde "
            + "                     INNER JOIN tde.statusAndamento tcd "
            + "                     INNER JOIN tcd.andamentoProcesso tcb "
            + "                 WHERE tde.processoAdministrativo.id = tdc.id "
            + "                     AND tde.situacao = :p2  "
            + "                     AND tcb.codigo = :p3 "
            + "                     AND tcb.ativo = :p1 "
            + "                     AND tde.ativo = :p1 "
            + "                     AND tcd.ativo = :p1)"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessoAdministrativoSuspensaoAtivoPontuacaoUltimo",
            query = "SELECT pa "
            + "FROM PAOcorrenciaStatus pao "
            + "INNER JOIN pao.processoAdministrativo pa "
            + "INNER JOIN pa.origemApoio tdh "
            + "INNER JOIN pao.statusAndamento sa "
            + "INNER JOIN sa.status s "
            + "INNER JOIN sa.andamentoProcesso ap "
            + "WHERE s.codigo NOT IN(:p1) "
            + "AND pa.cpf = :p0 "
            + "AND pa.ativo = :p2 "
            + "AND pao.ativo = :p2 "
            + "AND (:p4 IS NULL OR tdh.resultadoMotivo = :p4) "
            + "AND pa.tipo IN (:p3) "
            + "ORDER BY pa.dataProcessamento DESC "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessoAdministrativoSuspensaoAtivoBloqueioCnhMaisAntigo",
            query = "SELECT new br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper(tdc, "
            + "     (SELECT tcb.codigo FROM PAOcorrenciaStatus tde "
            + "         INNER JOIN tde.statusAndamento tcd "
            + "         INNER JOIN tcd.andamentoProcesso tcb "
            + "        where tde.ativo = :p0 "
            + "         and tde.processoAdministrativo.id = tdc.id))  "
            + "FROM MovimentoBloqueioBCA tdz "
            + " INNER JOIN tdz.bloqueioBCA tdk "
            + " INNER JOIN tdk.processoAdministrativo tdc "
            + "WHERE tdz.ativo = :p0 "
            + " AND tdz.tipo = :p1 "
            + "AND tdc.id IN (SELECT pa.id "
            + "                 FROM PAOcorrenciaStatus pao "
            + "                     INNER JOIN pao.processoAdministrativo pa "
            + "                     INNER JOIN pao.statusAndamento sa "
            + "                     INNER JOIN sa.status s "
            + "                     INNER JOIN sa.andamentoProcesso ap "
            + "                 WHERE s.codigo NOT IN (:p3) "
            + "                     AND pa.cpf = :p2 "
            + "                     AND pa.ativo = :p0 "
            + "                     AND pao.ativo = :p0 "
            + "                     AND pa.tipo IN (:p4)) "
            + "ORDER BY tdz.dataBCA "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListProcessoAdministrativosAtivosPorCPFEntidadeCompleta",
            query = "SELECT pa "
            + "FROM PAOcorrenciaStatus os "
            + "INNER JOIN os.processoAdministrativo pa "
            + "INNER JOIN os.statusAndamento sa "
            + "INNER JOIN sa.status s "
            + "WHERE pa.cpf = :p0 "
            + "AND s.codigo NOT IN (:p1) "
            + "AND pa.ativo = :p2 "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListProcessoAdministrativosAtivosPorNumeroDetranParaExaminador",
            query = "SELECT tcd.id "
            + " FROM PAOcorrenciaStatus tde "
            + "     INNER JOIN tde.processoAdministrativo tdc "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "     INNER JOIN tcd.status tca "
            + "     INNER JOIN tde.fluxoProcesso tck "
            + " where EXISTS ("
            + "         SELECT 1 "
            + "             From PAFluxoFase tch "
            + "                 INNER JOIN tch.prioridadeFluxoAmparo tci "
            + "                 INNER JOIN tci.faseProcessoAdm tcv "
            + "                 INNER JOIN tci.fluxoProcesso tck2 "
            + "             where tck.id = tck2.id "
            + "                 and (tcv.codigo not like :p1 AND tcv.codigo not like :p2)) "
            + "     AND tdc.numeroDetran = :p0 "
            + "     AND tca.codigo IN (:p3) "
            + "     AND tdc.ativo = :p4 "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessoAdministrativoComAndamentoProcesso137",
            query = "SELECT tdc "
            + "FROM ProcessoAdministrativo tdc "
            + "WHERE tdc.id IN (SELECT tde.processoAdministrativo.id "
            + "                 FROM PAOcorrenciaStatus tde "
            + "                 WHERE tde.statusAndamento.andamentoProcesso.codigo = :p0 "
            + "                 AND tde.situacao = :p1) "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessosAdministrativosAtivos",
            query = "SELECT tdc FROM ProcessoAdministrativo tdc WHERE tdc.ativo = :p0 "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListProcessoAdministrativoSuspensaoEmAndamento",
            query = "SELECT new ProcessoAdministrativo(pa.id) "
            + "FROM PAOcorrenciaStatus os "
            + "INNER JOIN os.processoAdministrativo pa "
            + "INNER JOIN os.statusAndamento sa "
            + "INNER JOIN sa.status s "
            + "WHERE pa.cpf = :p0 "
            + "AND s.codigo NOT IN (:p1) "
            + "AND pa.tipo IN (:p2) "
            + "AND pa.ativo = :p3 "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListProcessoAdministrativoAtivoParaCondutorReincidente",
            query = "SELECT new ProcessoAdministrativo(tdc.id) "
            + " FROM PAOcorrenciaStatus tde "
            + "     INNER JOIN tde.processoAdministrativo tdc "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "     INNER JOIN tcd.status tca "
            + " WHERE tdc.cpf = :p0 "
            + "     AND tca.codigo NOT IN (:p1) "
            + "     AND tdc.tipo IN (:p2) "
            + "     AND tdc.ativo = :p3 "),
    @NamedQuery(
            name = "ProcessoAdministrativo.findAll",
            query = "SELECT pa FROM ProcessoAdministrativo pa"),
    @NamedQuery(
            name = "ProcessoAdministrativo.getPAsInstauradosPorDia",
            query = "SELECT pa FROM ProcessoAdministrativo pa where pa.dataInclusao > :p0"),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessoAdministrativoAtivosParaPermissionado",
            query = "SELECT new ProcessoAdministrativo(pa.id) "
            + "FROM PAOcorrenciaStatus pao  "
            + " INNER JOIN pao.processoAdministrativo pa "
            + " INNER JOIN pao.statusAndamento sa "
            + " INNER JOIN sa.status s "
            + "WHERE s.codigo NOT IN(:p1) "
            + " and pa.cpf = :p0 "
            + " and pa.ativo = :p2"),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessosAdministrativosSuspensaoAtivos",
            query = "SELECT new br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper(pa.id, ap.codigo) "
            + " FROM PAOcorrenciaStatus pao "
            + "INNER JOIN pao.processoAdministrativo pa "
            + "INNER JOIN pa.origemApoio tdh "
            + "INNER JOIN pao.statusAndamento sa "
            + "INNER JOIN sa.status s "
            + "INNER JOIN sa.andamentoProcesso ap "
            + "WHERE s.codigo NOT IN(:p1) "
            + " and pa.cpf = :p0 "
            + " and pa.ativo = :p2 "
            + " and pao.ativo = :p2 "
            + " and (:p4 IS NULL OR tdh.resultadoMotivo = :p4) "
            + " and pa.tipo IN (:p3) "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessosAdministrativosSuspensaoAtivosComAndamentoPorCPF",
            query = "SELECT new br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper(pa.id, ap.codigo) "
            + "FROM PAOcorrenciaStatus pao "
            + " INNER JOIN pao.processoAdministrativo pa "
            + " INNER JOIN pao.statusAndamento sa "
            + " INNER JOIN sa.status s "
            + " INNER JOIN sa.andamentoProcesso ap "
            + "WHERE s.codigo NOT IN (:p1) "
            + " and pa.cpf = :p0 "
            + " and pa.ativo = :p2 "
            + " and pao.ativo = :p2 "
            + " and pa.tipo IN (:p3) "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessosAdministrativosPorCpfEAutoECodigoInfracaoComAndamento",
            query = "SELECT new br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper(pa.id, ap.codigo, s.codigo) "
            + "FROM PAOcorrenciaStatus pao "
            + " INNER JOIN pao.processoAdministrativo pa "
            + " INNER JOIN pao.statusAndamento sa "
            + " INNER JOIN sa.status s "
            + " INNER JOIN sa.andamentoProcesso ap "
            + "WHERE pa.cpf = :p0 "
            + " and pa.ativo = :p3 "
            + " and pao.ativo = :p3 "
            + " and EXISTS(SELECT 1 FROM ProcessoAdministrativoInfracao tdd "
            + "     INNER JOIN tdd.processoAdministrativo tdc WHERE tdc.id = pa.id and tdd.codigoInfracao = :p1 and tdd.autoInfracao = :p2 ) "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessoAdministrativoPorNumeroProcessoAtivo",
            query = "SELECT tdc From ProcessoAdministrativo tdc where tdc.numeroProcesso = :p0 and tdc.ativo = :p1"),
    @NamedQuery(
            name = "ProcessoAdministrativo.getPAControleCnhWrapperPorFluxoECpf",
            query = "SELECT "
            + "new br.gov.ms.detran.processo.administrativo.wrapper.PAControleCnhWrapper("
            + "         tdc, "
            + "         (SELECT MAX(tdj.numeroPortaria) "
            + "                FROM NotificacaoComplemento tdj "
            + "                     INNER JOIN tdj.notificacao tcx "
            + "                where tcx.processoAdministrativo.id = tdc.id "
            + "                     and tcx.ativo = :p2 "
            + "                     and tdj.ativo = 1), "
            + "         (SELECT tdl.valor "
            + "                FROM PAComplemento tdl "
            + "                where tdl.processoAdministrativo.id = tdc.id "
            + "                     and tdl.ativo = :p2 "
            + "                     and tdl.parametro = :p3)"
            + " ) "
            + " From PAOcorrenciaStatus tde "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "     INNER JOIN tde.processoAdministrativo tdc "
            + "     INNER JOIN tcd.andamentoProcesso tcb "
            + "     INNER JOIN tde.fluxoProcesso tck "
            + " WHERE EXISTS("
            + "         SELECT 1 "
            + "         FROM PAFluxoAndamento tcr "
            + "             INNER JOIN tcr.fluxoFase tch "
            + "             INNER JOIN tch.prioridadeFluxoAmparo tci "
            + "             INNER JOIN tci.fluxoProcesso tck1 "
            + "             INNER JOIN tcr.fluxoProcesso tck2 "
            + "         where "
            + "             tck.id = tck1.id "
            + "             and tch.andamentoProcesso.id = tcb.id "
            + "             and tck2.codigo = :p1 "
            + "             and tcr.ativo = :p2 "
            + "             and tck2.ativo = :p2 "
            + "             and tch.ativo = :p2 "
            + "             and tci.ativo = :p2 "
            + "             and tck1.ativo = :p2) "
            + "     and tdc.cpf = :p0 "
            + "     AND (tdc.origemApoio.regra = :p5 OR  EXISTS(SELECT 1 "
            + "                FROM NotificacaoComplemento tdj "
            + "                     INNER JOIN tdj.notificacao tcx "
            + "                where tcx.processoAdministrativo.id = tdc.id "
            + "                     and tcx.tipoNotificacaoProcesso <> :p4"
            + "                     and tcx.ativo = :p2 "
            + "                     and tdj.ativo = :p2)) "),
    @NamedQuery(
            name = "ProcessoAdministrativo.validarPAAptoIniciarFluxo",
            query = "SELECT 1 "
            + " From PAOcorrenciaStatus tde "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "     INNER JOIN tde.processoAdministrativo tdc "
            + "     INNER JOIN tcd.andamentoProcesso tcb "
            + "     INNER JOIN tde.fluxoProcesso tck "
            + " WHERE EXISTS("
            + "         SELECT 1 "
            + "         FROM PAFluxoAndamento tcr "
            + "             INNER JOIN tcr.fluxoFase tch "
            + "             INNER JOIN tch.prioridadeFluxoAmparo tci "
            + "             INNER JOIN tci.fluxoProcesso tck1 "
            + "             INNER JOIN tcr.fluxoProcesso tck2 "
            + "         where "
            + "             tck.id = tck1.id "
            + "             and tch.andamentoProcesso.id = tcb.id "
            + "             and tck2.codigo = :p1 "
            + "             and tcr.ativo = :p2 "
            + "             and tck2.ativo = :p2 "
            + "             and tch.ativo = :p2 "
            + "             and tci.ativo = :p2 "
            + "             and tck1.ativo = :p2) "
            + "     and tdc.id = :p0 "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessoAdministrativoPorIDApoioOrigemInstauraucao",
            query = "SELECT tdc FROM ProcessoAdministrativo tdc WHERE tdc.origemApoio.id = :p0 AND tdc.ativo = :p1"),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessosAdministrativosCassacaoComBloqueio",
            query = "SELECT tdc  "
            + " From BloqueioBCA tdk "
            + "     INNER JOIN tdk.processoAdministrativo tdc"
            + " where NOT EXISTS ("
            + "         SELECT tdl "
            + "         FROM PAComplemento tdl "
            + "         where tdl.processoAdministrativo.id = tdc.id "
            + "             and tdl.ativo = :p2 "
            + "             and tdl.parametro = :p3) "
            + "     and tdc.tipo in (:p0) "
            + "     and tdk.situacao = :p1 "
            + "     and tdk.ativo = :p2 "
            + "     and tdc.ativo = :p2"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getPASuspensaoSemPenalidadePorCPF",
            query = "SELECT tdc  "
            + " FROM PAOcorrenciaStatus tde "
            + "     INNER JOIN tde.processoAdministrativo tdc "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "       inner join tcd.andamentoProcesso tcb "
            + "     INNER JOIN tcd.status tca "
            + "     INNER JOIN tde.fluxoProcesso tck "
            + " where EXISTS ("
            + "         SELECT 1 "
            + "             From PAFluxoFase tch "
            + "                 INNER JOIN tch.prioridadeFluxoAmparo tci "
            + "                 INNER JOIN tci.faseProcessoAdm tcv "
            + "                 INNER JOIN tci.fluxoProcesso tck2 "
            + "             where tck.id = tck2.id "
            + "                 and (tcv.codigo not like :p4 AND tcv.codigo not like :p5)) "
            + "     and NOT EXISTS ("
            + "             SELECT 1 "
            + "             FROM PAPenalidadeProcesso tea "
            + "             where tea.processoAdministrativo.id = tdc.id "
            + "                 and tea.ativo = :p2) "
            + "     and  NOT EXISTS ("
            + "           SELECT 1 "
            + "             From ProcessoAdministrativoApensado tdv "
            + "             where tdv.processoAdministrativo.id = tdc.id "
            + "                 and tdv.ativo = :p2) "
            + "     and tdc.tipo = :p0 "
            + "     and tdc.cpf = :p1 "
            + "     and tdc.ativo = :p2  "
            + "     and tde.ativo = :p2  "
            + "     and tca.codigo in (:p3)"
            + "       and tcb.codigo <> :p6"
    ),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessosCassacaoPorCPF",
            query = "Select new ProcessoAdministrativo(tdc.id) "
            + " From PAOcorrenciaStatus tde "
            + "     inner join tde.processoAdministrativo tdc "
            + "     INNER JOIN tde.statusAndamento tcd "
            + "     INNER JOIN tcd.status tca "
            + " where tdc.cpf = :p0 "
            + "     and tca.codigo = :p1 "
            + "     and tdc.tipo in (:p2) "),
    @NamedQuery(
            name = "ProcessoAdministrativo.getProcessosSuspensaoComBloqueioPorCPF",
            query = "Select new ProcessoAdministrativo(tdc.id) "
            + " From BloqueioBCA tdk "
            + "     INNER JOIN tdk.processoAdministrativo tdc "
            + " where tdc.cpf = :p0 "
            + "     and Exists("
            + "                 Select 1 "
            + "                 From PAOcorrenciaStatus tde "
            + "                     inner join tde.statusAndamento tcd "
            + "                     inner join tcd.status tca "
            + "                 where tca.codigo = :p1 "
            + "                     and tde.processoAdministrativo.id = tdc.id) "
            + "     and tdc.tipo in (:p2) "
            + "     and tdk.situacao = :p3 "
            + "     and tdk.ativo = :p4"),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListProcessosComPrazoNotificacaoExpirado",
            query = "SELECT "
            + " tdc "
            + "FROM NotificacaoProcessoAdministrativo tcx "
            + " INNER JOIN tcx.processoAdministrativo tdc "
            + " INNER JOIN tcx.fluxoFase tch "
            + " INNER JOIN tch.prioridadeFluxoAmparo tci "
            + " INNER JOIN tci.faseProcessoAdm tcv  "
            + "WHERE tcx.ativo = :p2 and "
            + " EXISTS("
            + "     SELECT 1 "
            + "     FROM PAOcorrenciaStatus tde "
            + "         INNER JOIN tde.statusAndamento tcd "
            + "         INNER JOIN tcd.andamentoProcesso tcb  "
            + "     WHERE tdc.id = tde.processoAdministrativo.id "
            + "         AND tci.fluxoProcesso.id = tde.fluxoProcesso.id "
            + "         AND EXISTS("
            + "                 SELECT 1 "
            + "                 FROM PAFluxoFase tch1 "
            + "                     INNER JOIN tch1.prioridadeFluxoAmparo tci1 "
            + "                     INNER JOIN tci1.faseProcessoAdm tcv1 "
            + "                 WHERE tcv1.id = tcv.id "
            + "                     and tch1.andamentoProcesso.id = tcb.id) "
            + "         AND tcb.codigo in (:p0) "
            + "         AND tde.situacao = :p1) "
            + " AND not exists (select 1 "
            + "                   From RecursoPAOnline r  "
            + "                    inner join r.processoAdministrativo p "
            + "                   where p.id = tdc.id and r.situacao = :p4 and r.ativo = :p2) "
            + " AND tcx.dataPrazoLimite < :p3"),
    @NamedQuery(
            name = "ProcessoAdministrativo.buscarProcessosAdministrativosNoAndamento48ComPenaCumprida",
            query = "SELECT tdc from PAOcorrenciaStatus tde INNER JOIN tde.processoAdministrativo tdc INNER JOIN tde.statusAndamento tcd INNER JOIN tcd.andamentoProcesso tcb where tcb.codigo = :p0 and tde.ativo = :p1 and tcd.ativo = :p1 and EXISTS(SELECT 1 From PAPenalidadeProcesso tea INNER JOIN tea.processoAdministrativo tdc1 where tdc.id = tdc1.id and tea.ativo = :p1 and tea.dataFimPenalidade < :p2)"),
    @NamedQuery(
            name = "ProcessoAdministrativo.getListaProcessosFilho",
            query = "SELECT tdc "
                    + "FROM ProcessoAdministrativoApensado tdv  "
                    + "inner join tdv.processoAdministrativoCassacao tdu  "
                    + "JOIN tdu.processoAdministrativo tdc "
                    + "where tdv.processoAdministrativo.id = :p0 ")        
})
@NamedNativeQueries({
    @NamedNativeQuery(
            name = "ProcessoAdministrativo.getProcessosAdministrativosAndamento48paraCnhSituacaoEntrega",
            query = "SELECT "
            + "tdc.* "
            + "FROM dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc "
            + "WHERE tdc.Tdc_Cpf IN (SELECT baa.Baa_CPF_Entrega  "
            + "                      FROM dbo.TB_BAC_CNH_SITUACAO_ENTREGA bac "
            + "                      INNER JOIN dbo.TB_BAB_CNH_CONTROLE bab ON bab.Bab_ID = bac.Bac_CNH_Controle AND bab.Ativo = :p0 "
            + "                      INNER JOIN dbo.TB_BAA_CNH_CONTROLE_PESSOA baa ON baa.Baa_ID = bac.Bac_Pessoa_Nome AND baa.Ativo = :p0 "
            + "                      AND bac.Ativo = :p0 "
            + "                      AND bac.Bac_Acao = :p1) "
            + "AND tdc.Tdc_Cpf NOT IN (SELECT baa.Baa_CPF_Entrega  "
            + "                        FROM dbo.TB_BAC_CNH_SITUACAO_ENTREGA bac "
            + "                        INNER JOIN dbo.TB_BAB_CNH_CONTROLE bab ON bab.Bab_ID = bac.Bac_CNH_Controle AND bab.Ativo = :p0 "
            + "                        INNER JOIN dbo.TB_BAA_CNH_CONTROLE_PESSOA baa ON baa.Baa_ID = bac.Bac_Pessoa_Nome AND baa.Ativo = :p0 "
            + "                        AND bac.Ativo = :p0 "
            + "                        AND bac.Bac_Acao = :p2) "
            + "AND tdc.Tdc_ID IN (SELECT tde.Tde_Processo_Administrativo FROM dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde "
            + "                   INNER JOIN dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd ON tcd.Tcd_ID = tde.Tde_Status_Andamento AND tcd.Ativo = :p0 "
            + "                   INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb ON tcb.Tcb_ID = tcd.Tcd_Andamento_Processo AND tcd.Ativo = :p0 "
            + "                   WHERE tde.Ativo = :p0 "
            + "                   AND tcb.Tcb_Codigo = :p3 "
            + "                   AND tde.Tde_Situacao = :p4) "
            + "AND tdc.Ativo = :p0 ",
            resultClass = ProcessoAdministrativo.class),
    @NamedNativeQuery(
            name = "ProcessoAdministrativo.getPAsParaResultadoProvaPorCondutor",
            query = "SELECT tdc.*  "
            + "FROM dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde "
            + "	INNER join dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc on tdc.Tdc_ID = tde.Tde_Processo_Administrativo "
            + "	INNER JOIN dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd on tde.Tde_Status_Andamento = tcd.Tcd_ID "
            + "	INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb on tcd.Tcd_Andamento_Processo = tcb.Tcb_ID "
            + " INNER JOIN dbo.TB_TCA_PAD_STATUS tca on tcd.Tcd_Status = tca.Tca_ID "
            + "	INNER JOIN dbo.TB_TCK_PAD_FLUXO_PROCESSO tck on tde.Tde_Fluxo_Processo = tck.Tck_ID "
            + "	INNER join dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO tci on tci.Tci_Fluxo_Processo = tck.Tck_ID "
            + "	INNER JOIN dbo.TB_TCH_PAD_FLUXO_FASE tch on tch.Tch_Prioridade_Fluxo_Amparo = tci.Tci_ID "
            + "	INNER join dbo.TB_TCV_FASE_PROCESSO_ADM tcv on tci.Tci_Fase_Processo_Adm = tcv.Tcv_ID "
            + "	where tch.Tch_Andamento = tcb.Tcb_ID "
            + "			and tdc.Tdc_Numero_Detran = :p0 "
            + "			and tcb.Tcb_Codigo <> :p1 "
            + "			and tcv.Tcv_Codigo not like :p2  "
            + "			and tcv.Tcv_Codigo not like :p3 "
            + "			and tca.Tca_Codigo NOT IN (:p5) "
            + "			and not EXISTS( "
            + "					SELECT 1  "
            + "					From dbo.TB_TDY_RESULTADO_LAUDO_PAD tdy  "
            + "					where tdy.Tdy_Processo_Administrativo = tdc.Tdc_ID  "
            + "							and tdy.Ativo = :p4) ",
            resultClass = ProcessoAdministrativo.class),
    @NamedNativeQuery(
            name = "ProcessoAdministrativo.getListPAsAtivosBloqueio",
            query = "SELECT "
            + "tdc.* "
            + "FROM dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc "
            + "INNER JOIN dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde ON tde.Tde_Processo_Administrativo = tdc.Tdc_ID "
            + "INNER JOIN dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd ON tcd.Tcd_ID = tde.Tde_Status_Andamento "
            + "INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb ON tcb.Tcb_ID = tcd.Tcd_Andamento_Processo "
            + "WHERE tcb.Tcb_Codigo IN (156,155,154,147,64,43,44,137,48,45,138,60,139,157,158) "
            + "AND tdc.Tdc_Cpf =  :p0 ",
            resultClass = ProcessoAdministrativo.class),
    @NamedNativeQuery(
            name = "ProcessoAdministrativo.getListPAsAtivosPorNumAutoECodigoInfracaoECpfEAutuador",
            query = "SELECT tdc.* "
            + "FROM dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde "
            + " INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tde.Tde_Processo_Administrativo = tdc.Tdc_ID "
            + " INNER JOIN dbo.TB_TDD_PAD_INFRACAO_PROCESSO_ADMINISTRATIVO tdd ON tdd.Tdd_Processo_Administrativo = tdc.Tdc_ID "
            + " INNER JOIN dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd ON tde.Tde_Status_Andamento = tcd.Tcd_ID "
            + " INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb on tcd.Tcd_Andamento_Processo = tcb.Tcb_ID "
            + " INNER JOIN dbo.TB_TCA_PAD_STATUS tca ON tca.Tca_ID = tcd.Tcd_Status AND tca.Tca_Codigo IN (1,24) "
            + " INNER JOIN dbo.TB_TCK_PAD_FLUXO_PROCESSO tck ON tck.Tck_ID = tde.Tde_Fluxo_Processo "
            + " INNER JOIN dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO tci on tci.Tci_Fluxo_Processo = tck.Tck_ID "
            + " INNER JOIN dbo.TB_TCV_FASE_PROCESSO_ADM tcv ON tcv.Tcv_ID = tci.Tci_Fase_Processo_Adm AND (tcv.Tcv_Codigo NOT LIKE (:p5) AND tcv.Tcv_Codigo NOT LIKE (:p6) ) "
            + " INNER JOIN dbo.TB_TCH_PAD_FLUXO_FASE tch ON tch.Tch_Prioridade_Fluxo_Amparo = tci.Tci_ID AND tch.Tch_Andamento = tcb.Tcb_ID "
            + "WHERE (:p0 IS NULL OR tdd.Tdd_Auto_Infracao = :p0) "
            + " AND (:p1 IS NULL OR tdd.Tdd_Codigo_Infracao LIKE :p1) "
            + " AND (:p2 IS NULL OR tdc.Tdc_Cpf = :p2) "
            + " AND (:p3 IS NULL OR tdd.Tdd_Orgao_Autuador = :p3) "
            + " AND tdc.Ativo = :p4 "
            + " AND tdd.Ativo = :p4 "
            + " AND tde.Ativo = :p4",
            resultClass = ProcessoAdministrativo.class),
    @NamedNativeQuery(
            name = "ProcessoAdministrativo.getListPAsAtivosSemSuspensaoPorNumAutoECodigoInfracaoECpfEAutuador",
            query = "SELECT tdc.* "
            + "FROM dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde "
            + " INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tde.Tde_Processo_Administrativo = tdc.Tdc_ID "
            + " INNER JOIN dbo.TB_TDD_PAD_INFRACAO_PROCESSO_ADMINISTRATIVO tdd ON tdd.Tdd_Processo_Administrativo = tdc.Tdc_ID "
            + " INNER JOIN dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd ON tde.Tde_Status_Andamento = tcd.Tcd_ID "
            + " INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb on tcd.Tcd_Andamento_Processo = tcb.Tcb_ID "
            + " INNER JOIN dbo.TB_TCA_PAD_STATUS tca ON tca.Tca_ID = tcd.Tcd_Status AND tca.Tca_Codigo IN (1,24) "
            + " INNER JOIN dbo.TB_TCK_PAD_FLUXO_PROCESSO tck ON tck.Tck_ID = tde.Tde_Fluxo_Processo "
            + " INNER JOIN dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO tci on tci.Tci_Fluxo_Processo = tck.Tck_ID "
            + " INNER JOIN dbo.TB_TCV_FASE_PROCESSO_ADM tcv ON tcv.Tcv_ID = tci.Tci_Fase_Processo_Adm AND (tcv.Tcv_Codigo NOT LIKE (:p5) AND tcv.Tcv_Codigo NOT LIKE (:p7) AND tcv.Tcv_Codigo NOT LIKE (:p6) ) "
            + " INNER JOIN dbo.TB_TCH_PAD_FLUXO_FASE tch ON tch.Tch_Prioridade_Fluxo_Amparo = tci.Tci_ID AND tch.Tch_Andamento = tcb.Tcb_ID "
            + "WHERE (:p0 IS NULL OR tdd.Tdd_Auto_Infracao = :p0) "
            + " AND (:p1 IS NULL OR tdd.Tdd_Codigo_Infracao LIKE :p1) "
            + " AND (:p2 IS NULL OR tdc.Tdc_Cpf = :p2) "
            + " AND (:p3 IS NULL OR tdd.Tdd_Orgao_Autuador = :p3) "
            + " AND tdc.Ativo = :p4 "
            + " AND tdd.Ativo = :p4 "
            + " AND tde.Ativo = :p4",
            resultClass = ProcessoAdministrativo.class),
    @NamedNativeQuery(
            name = "ProcessoAdministrativo.getListPAsFaseArquivamentoPorNumAutoECodigoInfracaoECpf",
            query = "SELECT tdc.* "
            + "FROM dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde "
            + " INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tde.Tde_Processo_Administrativo = tdc.Tdc_ID "
            + " INNER JOIN dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd ON tde.Tde_Status_Andamento = tcd.Tcd_ID "
            + " INNER JOIN dbo.TB_TDD_PAD_INFRACAO_PROCESSO_ADMINISTRATIVO tdd ON tdd.Tdd_Processo_Administrativo = tdc.Tdc_ID "
            + " INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb ON tcd.Tcd_Andamento_Processo = tcb.Tcb_ID "
            + " INNER JOIN dbo.TB_TCA_PAD_STATUS tca ON tca.Tca_ID = tcd.Tcd_Status "
            + " INNER JOIN dbo.TB_TCK_PAD_FLUXO_PROCESSO tck ON tck.Tck_ID = tde.Tde_Fluxo_Processo "
            + " INNER JOIN TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO tci ON tci.Tci_Fluxo_Processo = tck.Tck_ID "
            + " INNER JOIN TB_TCV_FASE_PROCESSO_ADM tcv ON tcv.Tcv_ID = tci.Tci_Fase_Processo_Adm AND tcv.Tcv_Codigo LIKE :p3 "
            + " INNER JOIN TB_TCH_PAD_FLUXO_FASE tch ON tch.Tch_Prioridade_Fluxo_Amparo = tci.Tci_ID AND tch.Tch_Andamento = tcb.Tcb_ID "
            + "WHERE tdd.Tdd_Auto_Infracao = :p0 "
            + " AND tdd.Tdd_Codigo_Infracao LIKE :p1 "
            + " AND tdc.Tdc_Cpf = :p2 ",
            resultClass = ProcessoAdministrativo.class),
    @NamedNativeQuery(
            name = "ProcessoAdministrativo.validarArtigoInfracaoDiferenteArtigoInfracaoPAReincidente",
            query = "SELECT Distinct tdc.*   "
            + "	FROM dbo.TB_TDD_PAD_INFRACAO_PROCESSO_ADMINISTRATIVO tdd  "
            + "		INNER join dbo.TB_MBA_ACAO_INFRACAO_PENALIDADE mba on tdd.Tdd_Acao_Infracao_Penalidade = mba.Mba_ID  "
            + "		INNER join dbo.TB_MAA_TABELA_INFRACAO maa on mba.Mba_Tabela_Infracao = maa.Maa_ID "
            + "		INNER JOIN dbo.TB_MBT_AMPARO_LEGAL mbt on mbt.Mbt_ID = maa.Maa_Amparo_Legal "
            + "		INNER join dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc on tdd.Tdd_Processo_Administrativo = tdc.Tdc_ID  "
            + "	where tdc.Tdc_ID in (:p1) and tdc.Ativo = :p2 and tdd.Ativo = :p2 and maa.Ativo = 1  "
            + "		AND EXISTS ( "
            + "				SELECT 1 FROM dbo.TB_MAA_TABELA_INFRACAO maa2 "
            + "						INNER join dbo.TB_MBT_AMPARO_LEGAL mbt2 on mbt2.Mbt_ID = maa2.Maa_Amparo_Legal "
            + "					where maa2.Maa_Codigo = :p0 and mbt.Mbt_Artigo <> mbt2.Mbt_Artigo and maa2.Ativo = :p2)",
            resultClass = ProcessoAdministrativo.class),
    @NamedNativeQuery(
            name = "ProcessoAdministrativo.validarArtigoInfracaoIgualArtigoInfracaoPAReincidente",
            query = "SELECT Distinct tdc.*   "
            + "	FROM dbo.TB_TDD_PAD_INFRACAO_PROCESSO_ADMINISTRATIVO tdd  "
            + "		INNER join dbo.TB_MBA_ACAO_INFRACAO_PENALIDADE mba on tdd.Tdd_Acao_Infracao_Penalidade = mba.Mba_ID  "
            + "		INNER join dbo.TB_MAA_TABELA_INFRACAO maa on mba.Mba_Tabela_Infracao = maa.Maa_ID "
            + "		INNER JOIN dbo.TB_MBT_AMPARO_LEGAL mbt on mbt.Mbt_ID = maa.Maa_Amparo_Legal "
            + "		INNER join dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc on tdd.Tdd_Processo_Administrativo = tdc.Tdc_ID  "
            + "	where tdc.Tdc_ID in (:p1) and tdc.Ativo = :p2 and tdd.Ativo = :p2 and maa.Ativo = 1  "
            + "		AND EXISTS ( "
            + "				SELECT 1 FROM dbo.TB_MAA_TABELA_INFRACAO maa2 "
            + "						INNER join dbo.TB_MBT_AMPARO_LEGAL mbt2 on mbt2.Mbt_ID = maa2.Maa_Amparo_Legal "
            + "					where maa.Maa_Codigo = :p0 and mbt.Mbt_Artigo = mbt2.Mbt_Artigo and maa2.Ativo = :p2)",
            resultClass = ProcessoAdministrativo.class),
    @NamedNativeQuery(
            name = "ProcessoAdministrativo.verificaSeExisteProcessoAdmPorNumeroAutoEOraoAutuador",
            query = "SELECT  tdc.* "
            + " FROM TB_TCH_PAD_FLUXO_FASE tch INNER JOIN "
            + " TB_TCB_PAD_ANDAMENTO_PROCESSO tcb ON tcb.Tcb_ID = tch.Tch_Andamento INNER JOIN "
            + " TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO tci ON tch.Tch_Prioridade_Fluxo_Amparo = tci.Tci_ID INNER JOIN "
            + " TB_TCV_FASE_PROCESSO_ADM tcv ON tci.Tci_Fase_Processo_Adm = tcv.Tcv_ID AND tcv_codigo NOT LIKE ('9905%') AND "
            + " tcv_codigo NOT LIKE ('9904%') INNER JOIN "
            + " TB_TCK_PAD_FLUXO_PROCESSO tck ON tci.Tci_Fluxo_Processo = tck.Tck_ID inner JOIN "
            + " TB_TCD_PAD_STATUS_ANDAMENTO TCD ON tcb.Tcb_ID = tcd.Tcd_Andamento_Processo INNER JOIN  "
            + " TB_TCA_PAD_STATUS TCA ON TCD.Tcd_Status = TCA.Tca_ID AND TCA.Tca_Codigo IN (1) AND tca.Ativo = 1 INNER JOIN "
            + " TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM TDE ON TCD.Tcd_ID = TDE.Tde_Status_Andamento AND tde.Tde_Fluxo_Processo = tck.Tck_ID INNER join "
            + " TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON TDE.Tde_Processo_Administrativo = tdc.Tdc_ID INNER JOIN "
            + " TB_TDD_PAD_INFRACAO_PROCESSO_ADMINISTRATIVO TDD ON tdc.Tdc_ID = TDD.Tdd_Processo_Administrativo "
            + " WHERE tdd.Tdd_Auto_Infracao = :p0 AND tdd.Tdd_Orgao_Autuador = :p1 ",
            resultClass = ProcessoAdministrativo.class),
    @NamedNativeQuery(
            name = "ProcessoAdministrativo.getProcessosAndamento140ComPenaCumpridaParaWSEntregaCNH",
            query = "SELECT tdc.* "
            + "FROM dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde "
            + "INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tde.Tde_Processo_Administrativo = tdc.Tdc_ID "
            + "INNER JOIN dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd ON tde.Tde_Status_Andamento = tcd.Tcd_ID "
            + "INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb ON tcd.Tcd_Andamento_Processo = tcb.Tcb_ID "
            + "INNER JOIN dbo.TB_TEA_PAD_PENALIDADE_PROCESSO_PAT tea ON tea.Tea_Processo_Administrativo = tdc.Tdc_ID "
            + "WHERE tcb.Tcb_Codigo  = 140 "
            + "AND tea.Tea_Data_Fim_Penalidade < getdate() "
            + "AND tdc.Ativo = 1 ",
            resultClass = ProcessoAdministrativo.class)

})
public class ProcessoAdministrativo extends BaseEntityAtivo implements Serializable, IJSPASequencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdc_ID")
    private Long id;

    @Column(name = "Tdc_Cpf")
    private String cpf;

    @Column(name = "Tdc_Cnh")
    private Long cnh;

    @Column(name = "Tdc_Tipo_Processo")
    @Enumerated(EnumType.STRING)
    private TipoProcessoEnum tipo;

    @Column(name = "Tdc_Numero_Detran")
    private Long numeroDetran;

    @Column(name = "Tdc_Numero_Processo")
    private String numeroProcesso;

    @Column(name = "Tdc_Numero_Portaria")
    private String numeroPortaria;

    @Column(name = "Tdc_Data_Processamento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataProcessamento;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    @ManyToOne
    @JoinColumn(name = "Tdc_Apoio_Origem_Instauracao", referencedColumnName = "Tdh_ID")
    private ApoioOrigemInstauracao origemApoio;

    @Column(name = "Tdc_Atendimento")
    private Long atendimento;

    @Column(name = "Tdc_Permissionado")
    private String numeroPermissionado;

    @Column(name = "Tdc_Numero_Registro")
    private String numeroRegistro;

    @ManyToOne
    @JoinColumn(name = "Tdc_Execucao_Instauracao", referencedColumnName = "Ted_ID")
    private ExecucaoInstauracao execucaoInstauracao;

    @Column(name = "Tdc_Origem")
    @Enumerated(EnumType.STRING)
    private OrigemEnum origem;

    @Column(name = "Tdc_Municipio_Condutor")
    private Long municipioCondutor;

    public ProcessoAdministrativo() {
    }

    public ProcessoAdministrativo(Long id) {
        this.id = id;
    }

    public ProcessoAdministrativo(Long id, Long vr) {
        this(id);
        super.setVersaoRegistro(vr);
    }

    public ProcessoAdministrativo(Long idProcessoAdministrativoOrigem, String numeroProcessoAdministrativoOrigem) {
        this(idProcessoAdministrativoOrigem);
        this.numeroProcesso = numeroProcessoAdministrativoOrigem;
    }

    public ProcessoAdministrativo(Long id, String numeroProcesso, Long versaoRegistro) {
        this(id, numeroProcesso);
        super.setVersaoRegistro(versaoRegistro);
    }

    public ProcessoAdministrativo(Long id, String numeroProcesso, OrigemEnum origem, Long versaoRegistro) {
        this(id, numeroProcesso, versaoRegistro);
        this.origem = origem;
    }

    public ProcessoAdministrativo(Long id, String numeroProcesso, Long numeroDetran, String cpf, Long versaoRegistro) {
        this(id, versaoRegistro);
        this.numeroProcesso = numeroProcesso;
        this.numeroDetran = numeroDetran;
        this.cpf = cpf;
    }

    public ProcessoAdministrativo(Long id, String numeroProcesso, String cpf, Long cnh) {
        this.id = id;
        this.cpf = cpf;
        this.cnh = cnh;
        this.numeroProcesso = numeroProcesso;
    }

    public ProcessoAdministrativo(Long id, String numeroProcesso, String cpf, Long cnh, TipoProcessoEnum tipo) {

        this.id = id;
        this.numeroProcesso = numeroProcesso;
        this.cpf = cpf;
        this.cnh = cnh;
        this.tipo = tipo;
    }

    @Override
    public Long getId() {
        return id;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public void setId(Serializable id) {
        this.id = (id != null ? Long.valueOf(id.toString()) : null);
    }

    public String getCpf() {
        return cpf;
    }

    @XmlJavaTypeAdapter(CPFAdapter.class)
    @XmlElement(name = "cpf")
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Long getCnh() {
        return cnh;
    }

    public void setCnh(Long cnh) {
        this.cnh = cnh;
    }

    public TipoProcessoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoProcessoEnum tipo) {
        this.tipo = tipo;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public Long getNumeroDetran() {
        return numeroDetran;
    }

    public void setNumeroDetran(Long numeroDetran) {
        this.numeroDetran = numeroDetran;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroPortaria() {
        return numeroPortaria;
    }

    public void setNumeroPortaria(String numeroPortaria) {
        this.numeroPortaria = numeroPortaria;
    }

    public Date getDataProcessamento() {
        return dataProcessamento;
    }

    @XmlElement(name = "dataProcessamento")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataProcessamento(Date dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public ApoioOrigemInstauracao getOrigemApoio() {
        return origemApoio;
    }

    public void setOrigemApoio(ApoioOrigemInstauracao origemApoio) {
        this.origemApoio = origemApoio;
    }

    @Override
    public void setSequencial(Long sequencial) {
        this.numeroProcesso = sequencial == null ? "ERRO" : sequencial.toString();
    }

    public Long getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Long atendimento) {
        this.atendimento = atendimento;
    }

    public String getNumeroPermissionado() {
        return numeroPermissionado;
    }

    public void setNumeroPermissionado(String numeroPermissionado) {
        this.numeroPermissionado = numeroPermissionado;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public ExecucaoInstauracao getExecucaoInstauracao() {
        return execucaoInstauracao;
    }

    public void setExecucaoInstauracao(ExecucaoInstauracao execucaoInstauracao) {
        this.execucaoInstauracao = execucaoInstauracao;
    }

    @XmlElement(name = "numeroProcessoMascarado")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public String getNumeroProcessoMascarado() {
        return numeroProcesso;
    }

    public void setNumeroProcessoMascarado(String numeroProcesso) {
    }

    public OrigemEnum getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemEnum origem) {
        this.origem = origem;
    }

    public Boolean isPontuacao() {
        return OrigemEnum.PONTUACAO.equals(this.origem);
    }

    public Boolean isJuridico() {
        return OrigemEnum.JURIDICA.equals(this.origem);
    }

    public Boolean isManual() {
        return OrigemEnum.MANUAL.equals(this.origem);
    }

    public Boolean isOutraUf() {
        return OrigemEnum.OUTRA_UF.equals(this.origem);
    }

    public Long getMunicipioCondutor() {
        return municipioCondutor;
    }

    public void setMunicipioCondutor(Long municipioCondutor) {
        this.municipioCondutor = municipioCondutor;
    }
}
