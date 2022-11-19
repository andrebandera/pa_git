package br.gov.ms.detran.processo.administrativo.integracao;

import br.gov.ms.detran.comum.constantes.CodigoGrupoServico;
import br.gov.ms.detran.comum.core.projeto.entidade.ate.SituacaoAtendimento;
import br.gov.ms.detran.comum.core.projeto.enums.ate.SituacaoAtendimentoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import static br.gov.ms.detran.comum.util.DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO;
import static br.gov.ms.detran.comum.util.DetranStringUtil.preencherEspaco;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.integracao.comum.util.MapIntegracaoMainframeBuilder2;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP06;
import br.gov.ms.detran.integracao.comum.wrapper.EnvioMainframeWrapper;
import br.gov.ms.detran.integracao.comum.wrapper.MainFrameServiceWrapper;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAApoioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAStatusAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.enums.PAStatusEnum;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.EntityManager;

/**
 * @author Lillydi
 */
public class AEMNPP06BO {

    private Map<TipoPesquisaEnum, ITipoPesquisa> tiposPesquisa;

    private final EntityManager em;

    private IApoioService apoioService;

    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    IPAControleFalhaService paControleFalha;

    /**
     *
     * @return
     */
    IPAControleFalhaService getControleFalha() {
        if (paControleFalha == null) {
            paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
        }
        return paControleFalha;
    }

    public AEMNPP06BO(EntityManager em) {
        inicializa();

        this.em = em;
    }

    private void inicializa() {

        this.tiposPesquisa = new HashMap<>();

        this.tiposPesquisa.put(TipoPesquisaEnum.TIPO_1, new TipoPesquisa01());
        this.tiposPesquisa.put(TipoPesquisaEnum.TIPO_2, new TipoPesquisa02());
        this.tiposPesquisa.put(TipoPesquisaEnum.TIPO_3, new TipoPesquisa03());
        this.tiposPesquisa.put(TipoPesquisaEnum.TIPO_4, new TipoPesquisa04());
        this.tiposPesquisa.put(TipoPesquisaEnum.TIPO_5, new TipoPesquisa05());
        this.tiposPesquisa.put(TipoPesquisaEnum.TIPO_6, new TipoPesquisa06());
        this.tiposPesquisa.put(TipoPesquisaEnum.TIPO_7, new TipoPesquisa07());
        this.tiposPesquisa.put(TipoPesquisaEnum.TIPO_8, new TipoPesquisa08());
    }

    private ITipoPesquisa getTipoPesquisa(TipoPesquisaEnum tipoEnum) throws AppException {

        if (tipoEnum == null || tiposPesquisa == null || tiposPesquisa.isEmpty()) {
            DetranWebUtils.applicationMessageException("UC1154.M2", "", "Regra");
        }

        return this.tiposPesquisa.get(tipoEnum);
    }

    public Object processar(Object obj) {

        MainFrameServiceWrapper wrapper = (MainFrameServiceWrapper) obj;
        MapIntegracaoMainframeBuilder2 builder = new MapIntegracaoMainframeBuilder2(wrapper.getResultado());
        AEMNPP06 aemnpp06 = new AEMNPP06();
        builder.build(aemnpp06);

        EnvioMainframeWrapper retorno = new EnvioMainframeWrapper(
                wrapper.getCodigoPrograma(),
                CodigoGrupoServico.VEICULO,
                new ParametroEnvioIntegracao());
        try {

            validacoesGerais(aemnpp06);

            TipoPesquisaEnum tipoEnum = TipoPesquisaEnum.get(aemnpp06.getTipoPesquisa());

            if (getTipoPesquisa(tipoEnum).valida(aemnpp06)) {

                getTipoPesquisa(tipoEnum).processa(aemnpp06);
                retorno = montarRetorno(wrapper.getCodigoPrograma(), CodigoGrupoServico.VEICULO, aemnpp06);

            } else {
                retorno.setCodigoRetorno("001");
                retorno.setMsgErro(preencherEspaco("DADOS INFORMADOS INVALIDOS.", 89, ALFA_NUMERICO));
            }
        } catch (AppException e) {

            retorno.setCodigoRetorno("999");
            retorno.setMsgErro(preencherEspaco("NAO FOI POSSIVEL PROCESSAR A SOLICITACAO", 89, ALFA_NUMERICO));

            getControleFalha().gravarFalhaCondutor(e,
                    "Erro ao executar AEMNPP06",
                    aemnpp06 != null ? !DetranStringUtil.ehBrancoOuNulo(aemnpp06.getCpf()) ? aemnpp06.getCpf() : "CPF NULL" : "CPF NULL");

        }
        return retorno;
    }

    /**
     * @param aemnpp06
     * @throws AppException
     */
    private void validacoesGerais(AEMNPP06 aemnpp06) throws AppException {

        if (aemnpp06 == null || aemnpp06.getTipoPesquisa() == null) {
            DetranWebUtils.applicationMessageException("NAO FOI POSSIVEL PROCESSAR A SOLICITACAO.");
        }
    }

    /**
     * @param codigoPrograma
     * @param codigoServico
     * @param aemnpp06
     * @return
     */
    private EnvioMainframeWrapper montarRetorno(String codigoPrograma, Integer codigoServico, AEMNPP06 aemnpp06) {

        EnvioMainframeWrapper retorno;

        ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();
        params.adicionarParametro(DetranStringUtil.preencherEspaco(aemnpp06.getTipoPesquisa(), 2, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(aemnpp06.getNumeroAuto(), 10, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(aemnpp06.getCodigoInfracao(), 4, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(aemnpp06.getCpf(), DetranTamanhoConstante.CPF, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(aemnpp06.getOrgaoAutuador(), 6, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(aemnpp06.getSituacaoPesquisa(), 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(aemnpp06.getDescricaoSituacao(), 15, DetranStringUtil.TipoDadoEnum.ALFA));

        retorno = new EnvioMainframeWrapper(codigoPrograma, codigoServico, params);

        retorno.setCodigoRetorno("000");
        retorno.setMsgErro(DetranStringUtil.preencherEspaco("", DetranTamanhoConstante.MSG_ERR0, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));

        return retorno;
    }

    /**
     * @param list
     * @param aemnpp06
     */
    private void defineSituacaoPesquisa(List list, AEMNPP06 aemnpp06) {
        if (DetranCollectionUtil.ehNuloOuVazio(list)) {
            aemnpp06.setSituacaoPesquisa("1");
        } else {
            aemnpp06.setSituacaoPesquisa("2");
        }
    }

    private enum TipoPesquisaEnum {
        TIPO_1("01"),
        TIPO_2("02"),
        TIPO_3("03"),
        TIPO_4("04"),
        TIPO_5("05"),
        TIPO_6("06"),
        TIPO_7("07"),
        TIPO_8("08");

        private final String codigo;
        private static final Map<String, TipoPesquisaEnum> LOOKUP = new TreeMap();

        static {

            for (TipoPesquisaEnum s : EnumSet.allOf(TipoPesquisaEnum.class)) {
                LOOKUP.put(s.getCodigo(), s);
            }
        }

        private TipoPesquisaEnum(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }

        public static TipoPesquisaEnum get(String code) {
            return LOOKUP.get(code);
        }
    }

    private interface ITipoPesquisa {

        boolean valida(AEMNPP06 aemnpp06) throws AppException;

        void processa(AEMNPP06 aemnpp06) throws AppException;

    }

    private class TipoPesquisa01 implements ITipoPesquisa {

        @Override
        public boolean valida(AEMNPP06 aemnpp06) throws AppException {
            return !(DetranStringUtil.ehBrancoOuNulo(aemnpp06.getCpf())
                    || DetranStringUtil.ehBrancoOuNulo(aemnpp06.getNumeroAuto())
                    || DetranStringUtil.ehBrancoOuNulo(aemnpp06.getCodigoInfracao())
                    || DetranStringUtil.ehBrancoOuNulo(aemnpp06.getOrgaoAutuador()));
        }

        @Override
        public void processa(AEMNPP06 aemnpp06) throws AppException {

            Long idAutuador = new PAApoioRepositorio().getOrgaoAutuadorIdPeloCodigo(
                    em, Integer.parseInt(aemnpp06.getOrgaoAutuador()));

            List list
                    = new ProcessoAdministrativoRepositorio().
                            getListPAsAtivosPorNumAutoECodigoInfracaoECpfEAutuador(
                                    em,
                                    aemnpp06.getNumeroAuto(),
                                    aemnpp06.getCodigoInfracao(),
                                    aemnpp06.getCpf(),
                                    idAutuador);

            defineSituacaoPesquisa(list, aemnpp06);
        }
    }

    private class TipoPesquisa02 implements ITipoPesquisa {

        @Override
        public boolean valida(AEMNPP06 aemnpp06) throws AppException {
            return !(DetranStringUtil.ehBrancoOuNulo(aemnpp06.getNumeroAuto())
                    || DetranStringUtil.ehBrancoOuNulo(aemnpp06.getCodigoInfracao()));
        }

        @Override
        public void processa(AEMNPP06 aemnpp06) throws AppException {

            List list
                    = new ProcessoAdministrativoRepositorio().
                            getListPAsAtivosPorNumAutoECodigoInfracaoECpfEAutuador(
                                    em,
                                    aemnpp06.getNumeroAuto(),
                                    aemnpp06.getCodigoInfracao(),
                                    null,
                                    null);

            defineSituacaoPesquisa(list, aemnpp06);
        }
    }

    private class TipoPesquisa03 implements ITipoPesquisa {

        @Override
        public boolean valida(AEMNPP06 aemnpp06) throws AppException {
            return !(DetranStringUtil.ehBrancoOuNulo(aemnpp06.getNumeroAuto())
                    || DetranStringUtil.ehBrancoOuNulo(aemnpp06.getCodigoInfracao()));
        }

        @Override
        public void processa(AEMNPP06 aemnpp06) throws AppException {
            List list
                    = new ProcessoAdministrativoRepositorio().
                            getListPAsAtivosPorNumAutoECodigoInfracaoECpfEAutuador(
                                    em,
                                    aemnpp06.getNumeroAuto(),
                                    aemnpp06.getCodigoInfracao(),
                                    null,
                                    null);

            defineSituacaoPesquisa(list, aemnpp06);
        }
    }

    private class TipoPesquisa04 implements ITipoPesquisa {

        @Override
        public boolean valida(AEMNPP06 aemnpp06) throws AppException {
            return !(DetranStringUtil.ehBrancoOuNulo(aemnpp06.getCpf())
                    || DetranStringUtil.ehBrancoOuNulo(aemnpp06.getNumeroAuto())
                    || DetranStringUtil.ehBrancoOuNulo(aemnpp06.getCodigoInfracao()));
        }

        @Override
        public void processa(AEMNPP06 aemnpp06) throws AppException {

            ProcessoAdministrativoInfracao infracao = new ProcessoAdministrativoInfracaoRepositorio()
                    .getInfracaoPorNumeroAutoECodigoInfracaoOrderByAtivo(
                            em,
                            aemnpp06.getNumeroAuto(),
                            aemnpp06.getCodigoInfracao()
                    );

            if (infracao == null) { // Se o extrato não existir (numeroAuto + codigoInfracao).
                aemnpp06.setSituacaoPesquisa("1");

            } else {

                if (!aemnpp06.getCpf().equals(infracao.getProcessoAdministrativo().getCpf())) {
                    // Se o CPF não for o mesmo do Processo.
                    aemnpp06.setSituacaoPesquisa("1");

                } else {

                    SituacaoAtendimento situacaoAtendimento
                            = (SituacaoAtendimento) getApoioService()
                                    .getSituacaoAtendimentoPorAtendimentoId(infracao.getProcessoAdministrativo().getAtendimento());

                    if (situacaoAtendimento != null) {

                        if (SituacaoAtendimentoEnum.EM_ANDAMENTO.ordinal() == situacaoAtendimento.getStatusAtendimento().getCodigo()) {

                            aemnpp06.setSituacaoPesquisa("2");

                            PAStatusAndamento statusAndamento
                                    = new PAStatusAndamentoRepositorio()
                                            .getStatusPorProcessoAdministrativo(
                                                    em,
                                                    infracao.getProcessoAdministrativo().getId());

                            List list = new ProcessoAdministrativoRepositorio().
                                    getListPAsFaseArquivamentoPorNumAutoECodigoInfracaoECpf(
                                            em,
                                            aemnpp06.getNumeroAuto(),
                                            aemnpp06.getCodigoInfracao(),
                                            aemnpp06.getCpf());

                            if (!DetranCollectionUtil.ehNuloOuVazio(list)) {
                                // Se o Atendimento estiver Aberto e o Andamento estiver na fase de Arquivamento.
                                aemnpp06.setSituacaoPesquisa("1");
                            } else if (statusAndamento != null && PAStatusEnum.SUSPENSO.getCodigo() == statusAndamento.getStatus().getCodigo()) {
                                //Se o Atendimento estiver Aberto, e o Status do PA estiver Suspenso.
                                aemnpp06.setDescricaoSituacao("PA SUSPENS");

                            } else if (statusAndamento != null && PAStatusEnum.ATIVO.getCodigo() == statusAndamento.getStatus().getCodigo()) {
                                //Se o Atendimento estiver Aberto, e o Status do PA estiver Ativo.
                                aemnpp06.setDescricaoSituacao("PA ATIVO");
                            }

                        } else {

                            aemnpp06.setSituacaoPesquisa("2");

                            if (SituacaoAtendimentoEnum.CANCELADO.ordinal() == situacaoAtendimento.getStatusAtendimento().getCodigo()) {
                                //Se o Atendimento estiver Cancelado.
                                aemnpp06.setDescricaoSituacao("PA ANULADO");

                            } else if (SituacaoAtendimentoEnum.FECHADO_CONCLUIDO.ordinal() == situacaoAtendimento.getStatusAtendimento().getCodigo()) {
                                //Se o Atendimento estiver Fechado Concluído.
                                aemnpp06.setDescricaoSituacao("PA CONCLUSO");

                            } else {
                                aemnpp06.setDescricaoSituacao("");
                            }
                        }
                    }
                }
            }
        }
    }

    private class TipoPesquisa05 implements ITipoPesquisa {

        @Override
        public boolean valida(AEMNPP06 aemnpp06) throws AppException {
            return !(DetranStringUtil.ehBrancoOuNulo(aemnpp06.getCpf()));
        }

        @Override
        public void processa(AEMNPP06 aemnpp06) throws AppException {

            List list
                    = new ProcessoAdministrativoRepositorio().
                            getListPAsAtivosPorNumAutoECodigoInfracaoECpfEAutuador(
                                    em,
                                    null,
                                    null,
                                    aemnpp06.getCpf(),
                                    null);

            defineSituacaoPesquisaTipo05(list, aemnpp06);
        }

        private void defineSituacaoPesquisaTipo05(List<ProcessoAdministrativo> list, AEMNPP06 aemnpp06) {
            if (DetranCollectionUtil.ehNuloOuVazio(list)) {
                aemnpp06.setSituacaoPesquisa("1");
            } else if (list.get(0).getTipo().equals(TipoProcessoEnum.SUSPENSAO_JUDICIAL)
                    || list.get(0).getTipo().equals(TipoProcessoEnum.SUSPENSAO)) {

                aemnpp06.setSituacaoPesquisa("2");
            } else {
                aemnpp06.setSituacaoPesquisa("3");
            }
        }
    }

    private class TipoPesquisa06 implements ITipoPesquisa {

        @Override
        public boolean valida(AEMNPP06 aemnpp06) throws AppException {
            return !(DetranStringUtil.ehBrancoOuNulo(aemnpp06.getCpf()));
        }

        @Override
        public void processa(AEMNPP06 aemnpp06) throws AppException {

            List list = new ProcessoAdministrativoRepositorio().getListPAsAtivosBloqueio(em, aemnpp06.getCpf());

            defineSituacaoPesquisa(list, aemnpp06);
        }
    }

    private class TipoPesquisa07 implements ITipoPesquisa {

        @Override
        public boolean valida(AEMNPP06 aemnpp06) throws AppException {
            return !(DetranStringUtil.ehBrancoOuNulo(aemnpp06.getCpf()));
        }

        @Override
        public void processa(AEMNPP06 aemnpp06) throws AppException {

            List<ProcessoAdministrativo> lista
                    = new ProcessoAdministrativoRepositorio()
                            .getListaProcessosAdministrativosAtivosOuSuspensosPorCpfParaAEMNPP06(em, aemnpp06.getCpf());

            if (DetranCollectionUtil.ehNuloOuVazio(lista)) {
                aemnpp06.setSituacaoPesquisa("1");
            } else {
                aemnpp06.setSituacaoPesquisa("3");
            }

        }
    }

    private class TipoPesquisa08 implements ITipoPesquisa {

        @Override
        public boolean valida(AEMNPP06 aemnpp06) throws AppException {
            return !(DetranStringUtil.ehBrancoOuNulo(aemnpp06.getNumeroAuto())
                    || DetranStringUtil.ehBrancoOuNulo(aemnpp06.getCodigoInfracao()));
        }

        @Override
        public void processa(AEMNPP06 aemnpp06) throws AppException {

            List list
                    = new ProcessoAdministrativoRepositorio().
                            getListPAsAtivosSemSuspensaoPorNumAutoECodigoInfracaoECpfEAutuador(
                                    em,
                                    aemnpp06.getNumeroAuto(),
                                    aemnpp06.getCodigoInfracao(),
                                    null,
                                    null);

            defineSituacaoPesquisa(list, aemnpp06);
        }
    }

}
