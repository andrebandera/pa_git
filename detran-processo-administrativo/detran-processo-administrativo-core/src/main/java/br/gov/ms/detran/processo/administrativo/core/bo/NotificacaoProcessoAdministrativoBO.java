package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.Notificacao;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoArquivo;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoCorpoTexto;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.core.projeto.enums.apo.ModuloTipoArquivoEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
import br.gov.ms.detran.comum.projeto.constantes.TipoNumeroSequencialConstante;
import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import br.gov.ms.detran.comum.projeto.service.FabricaJSPASequencial;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCodigoBarraHelper;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranHTTPUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.core.iface.servico.adm.IAdministrativoService;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP08;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import static br.gov.ms.detran.processo.administrativo.constantes.TipoArquivoPAConstante.TIPO_NOTIFICACAO_INDIVIDUAL;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP08BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.*;
import br.gov.ms.detran.processo.administrativo.entidade.ArquivoPA;
import br.gov.ms.detran.processo.administrativo.entidade.EditalProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAEnderecoAlternativo;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PATipoCorpoAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoRecurso;
import br.gov.ms.detran.processo.administrativo.enums.ComissaoAnaliseEnum;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.ResultadoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoEnvolvidoPAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.PAMunicipioWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificadosWrapper;
import br.gov.ms.detran.protocolo.entidade.Correspondencia;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaEspecie;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaMeioEnvio;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * @author Lillydi
 */
public class NotificacaoProcessoAdministrativoBO extends FabricaJSPASequencial {

    private IApoioService apoioService;
    private IAdministrativoService administrativoService;

    private final DetranGenericRepository genericRepository;

    private static final Integer ORIGEM_DETRAN_EXTERNO = 3;
    private static final Integer REMESSA_CORREIOS = 3;
    private static final Integer ENVIADA = 2;
    private static final Integer QTE_ESPECIE = 1;
    private static final String SETOR = "DT/SEPEN";
    private static final String PREFIXO_OBJETO = "PADNOT";
    private static final Integer NOTIFICACAO_PA = 2;

    /**
     * @return
     */
    public IApoioService getApoioService() {

        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }

        return apoioService;
    }

    /**
     * @return
     */
    public IAdministrativoService getAdministrativoService() {

        if (administrativoService == null) {
            administrativoService = (IAdministrativoService) JNDIUtil.lookup("ejb/AdministrativoService");
        }

        return administrativoService;
    }

    public NotificacaoProcessoAdministrativoBO() {
        genericRepository = new DetranGenericRepository();
    }

    /**
     * Realizar as notificações de INSTAURAÇÃO, PENALIDADE e ENTREGA CNH.
     *
     * @param em
     * @param pa
     * @param tipo
     * @return
     * @throws AppException
     */
    public NotificacaoProcessoAdministrativo notificar(EntityManager em, ProcessoAdministrativo pa, TipoFasePaEnum tipo) throws AppException {

        if (pa == null) {
            DetranWebUtils.applicationMessageException("Processo administrativo não encontrado para o número informado.");
        }

        PATipoCorpoAndamento tipoCorpoAndamento = getTipoCorpoAndamentoParaNotificacao(em, tipo, pa);

        validarCorpoTextoAtivoPorCodigo(tipoCorpoAndamento);

        validarAndamentoTipoNotificacao(em, pa, tipo);

        return gerar(em, pa, tipoCorpoAndamento.getTipoCorpoTexto(), tipoCorpoAndamento.getTipoNotificacaoProcesso(), tipoCorpoAndamento.getPrazoNotificacao());
    }

    /**
     *
     * @param em
     * @param tipo
     * @param pa
     * @return
     * @throws DatabaseException
     * @throws AppException
     */
    private PATipoCorpoAndamento getTipoCorpoAndamentoParaNotificacao(EntityManager em, TipoFasePaEnum tipo, ProcessoAdministrativo pa) throws AppException {

        PATipoCorpoAndamento tipoCorpoAndamento
                = new PATipoCorpoAndamentoRepositorio()
                        .getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracao(
                                em,
                                tipo,
                                pa.getOrigemApoio()
                        );

        if (tipoCorpoAndamento == null) {
            DetranWebUtils.applicationMessageException("TipoCorpoAndamento não encontrado.");
        }

        return tipoCorpoAndamento;
    }

    /**
     *
     * @param em
     * @param tipo
     * @param pa
     * @param comissaoAnalise
     * @return
     * @throws DatabaseException
     * @throws AppException
     */
    private PATipoCorpoAndamento getTipoCorpoAndamentoParaNotificacao(EntityManager em, TipoFasePaEnum tipo, ProcessoAdministrativo pa, ComissaoAnaliseEnum comissaoAnalise, Notificacao corpoTexto) throws AppException {

        PATipoCorpoAndamento tipoCorpoAndamento
                = new PATipoCorpoAndamentoRepositorio()
                        .getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracaoEComissaoAnaliseETipoCorpoTexto(
                                em,
                                tipo,
                                pa.getOrigemApoio(),
                                comissaoAnalise,
                                corpoTexto.getTipoCorpoTexto()
                        );

        if (tipoCorpoAndamento == null) {
            DetranWebUtils.applicationMessageException("TipoCorpoAndamento não encontrado.");
        }

        return tipoCorpoAndamento;
    }

    public NotificacaoProcessoAdministrativo notificarJARI(EntityManager em,
            NotificaProcessoAdministrativoWrapper entrada,
            ProcessoAdministrativo pa, TipoFasePaEnum tipo) throws AppException {

        ResultadoRecurso resultado
                = new ResultadoRecursoRepositorio().
                        getResultadoRecursoPorProcessoAdministrativoESituacaoEDestino(em,
                                pa.getId(),
                                SituacaoRecursoEnum.JULGADO,
                                OrigemDestinoEnum.JARI);

        Notificacao corpoTexto = getCorpoTextoNotificacaoJARI(em, resultado);

        PATipoCorpoAndamento tipoCorpoAndamento = getTipoCorpoAndamentoParaNotificacao(em, tipo, pa, resultado.getComissaoAnalise(), corpoTexto);

        return gerar(em, pa, corpoTexto.getTipoCorpoTexto().getId(), TipoFasePaEnum.JARI, tipoCorpoAndamento.getPrazoNotificacao());
    }

    public PATipoCorpoAndamento obterTipoCorpoAndamentoJari(EntityManager em, ProcessoAdministrativo pa) throws AppException {
        ResultadoRecurso resultado
                = new ResultadoRecursoRepositorio().
                        getResultadoRecursoPorProcessoAdministrativoESituacaoEDestino(em,
                                pa.getId(),
                                SituacaoRecursoEnum.JULGADO,
                                OrigemDestinoEnum.JARI);

        Notificacao corpoTexto = getCorpoTextoNotificacaoJARI(em, resultado);

        return getTipoCorpoAndamentoParaNotificacao(em, TipoFasePaEnum.JARI, pa, resultado.getComissaoAnalise(), corpoTexto);
    }

    private Notificacao getCorpoTextoNotificacaoJARI(EntityManager em, ResultadoRecurso resultado) throws AppException {
        Notificacao corpoTexto = new Notificacao();

        if (resultado == null) {
            DetranWebUtils.applicationMessageException("Não foi possível localizar o Recurso da JARI.");
        }
        switch (resultado.getResultado()) {
            case ACOLHIDO:
            case PROVIDO:

                if (ComissaoAnaliseEnum.JARI_1.equals(resultado.getComissaoAnalise())) {
                    corpoTexto = (Notificacao) getApoioService().getEditalCorpoPorTipo(TipoNotificacaoEnum.PA_NOTIFICACAO_PROVIDO_1_JARI);
                }

                if (ComissaoAnaliseEnum.JARI_2.equals(resultado.getComissaoAnalise())) {
                    corpoTexto = (Notificacao) getApoioService().getEditalCorpoPorTipo(TipoNotificacaoEnum.PA_NOTIFICACAO_PROVIDO_2_JARI);
                }

                if (ComissaoAnaliseEnum.JARI_3.equals(resultado.getComissaoAnalise())) {
                    corpoTexto = (Notificacao) getApoioService().getEditalCorpoPorTipo(TipoNotificacaoEnum.PA_NOTIFICACAO_PROVIDO_3_JARI);
                }

                break;

            case NAO_ACOLHIDO:
            case IMPROVIDO:

                if (ComissaoAnaliseEnum.JARI_1.equals(resultado.getComissaoAnalise())) {
                    corpoTexto = (Notificacao) getApoioService().getEditalCorpoPorTipo(TipoNotificacaoEnum.PA_NOTIFICACAO_IMPROVIDO_1_JARI);
                }

                if (ComissaoAnaliseEnum.JARI_2.equals(resultado.getComissaoAnalise())) {
                    corpoTexto = (Notificacao) getApoioService().getEditalCorpoPorTipo(TipoNotificacaoEnum.PA_NOTIFICACAO_IMPROVIDO_2_JARI);
                }

                if (ComissaoAnaliseEnum.JARI_3.equals(resultado.getComissaoAnalise())) {
                    corpoTexto = (Notificacao) getApoioService().getEditalCorpoPorTipo(TipoNotificacaoEnum.PA_NOTIFICACAO_IMPROVIDO_3_JARI);
                }

                break;
            case NAO_CONHECIMENTO:

                if (ComissaoAnaliseEnum.JARI_1.equals(resultado.getComissaoAnalise())) {
                    corpoTexto = (Notificacao) getApoioService().getEditalCorpoPorTipo(TipoNotificacaoEnum.PA_NOTIFICACAO_NAO_CONHECIDO_1_JARI);
                }

                if (ComissaoAnaliseEnum.JARI_2.equals(resultado.getComissaoAnalise())) {
                    corpoTexto = (Notificacao) getApoioService().getEditalCorpoPorTipo(TipoNotificacaoEnum.PA_NOTIFICACAO_NAO_CONHECIDO_2_JARI);
                }

                if (ComissaoAnaliseEnum.JARI_3.equals(resultado.getComissaoAnalise())) {
                    corpoTexto = (Notificacao) getApoioService().getEditalCorpoPorTipo(TipoNotificacaoEnum.PA_NOTIFICACAO_NAO_CONHECIDO_3_JARI);
                }

                break;
        }
        return corpoTexto;
    }

    /**
     * Validar se existe corpo de texto ativo para gerar a notificação.
     *
     * @throws AppException
     */
    private void validarCorpoTextoAtivoPorCodigo(PATipoCorpoAndamento tipoCorpoAndamento) throws AppException {

        if (tipoCorpoAndamento == null || tipoCorpoAndamento.getTipoCorpoTexto() == null) {
            DetranWebUtils.applicationMessageException("Dados obrigatórios inválidos.");
        }

        if (!getApoioService()
                .existeAssinaturaParaCorpoTextoAtivaPorTipoCorpoTexto(tipoCorpoAndamento.getTipoCorpoTexto())) {

            DetranWebUtils.applicationMessageException(
                    "Não foi possível encontrar Corpo de Texto, assinatura e CorpoTextoAssinatura Ativos para o tipo {0}."
                    + "Favor entrar em contato com a DIRTI.", "", String.valueOf(tipoCorpoAndamento.getTipoCorpoTexto()));
        }
    }

    /**
     * Geração da NotificacaoProcessoAdministrativo para o
     * ProcessoAdministrativo.
     *
     * @param em
     * @param pa
     * @return
     * @throws AppException
     */
    private NotificacaoProcessoAdministrativo gerar(
            EntityManager em,
            ProcessoAdministrativo pa,
            Long idTipoCorpoTexto,
            TipoFasePaEnum tipoNotificacao,
            Integer prazoNotificacao) throws AppException {

        Correspondencia correspondencia
                = gravarCorrespondencia(em, pa, tipoNotificacao);

        return gerarNotificacao(em, pa, correspondencia, idTipoCorpoTexto, tipoNotificacao, prazoNotificacao);
    }

    /**
     * Manipulação dos dados que serão gravados para a
     * NotificacaoProcessoAdministrativo.
     *
     * @param em
     * @param pa
     * @param correspondencia
     * @param idTipoCorpoTexto
     * @param tipoNotificacao
     * @param prazoNotificacao
     * @return
     * @throws AppException
     */
    public NotificacaoProcessoAdministrativo gerarNotificacao(EntityManager em,
            ProcessoAdministrativo pa,
            Correspondencia correspondencia,
            Long idTipoCorpoTexto,
            TipoFasePaEnum tipoNotificacao,
            Integer prazoNotificacao) throws AppException {

        NotificacaoProcessoAdministrativo notificacaoProcessoAdministrativo = new NotificacaoProcessoAdministrativo();

        notificacaoProcessoAdministrativo.setDataNotificacao(Calendar.getInstance().getTime());

        notificacaoProcessoAdministrativo.setDataPrazoLimite(Utils.addDayMonth(notificacaoProcessoAdministrativo.getDataNotificacao(), prazoNotificacao));

        notificacaoProcessoAdministrativo.setProcessoAdministrativo(pa);

        notificacaoProcessoAdministrativo
                .setFluxoFase(new PAFluxoFaseRepositorio().getFluxoFaseDoProcessoAdministrativo(em, pa));

        notificacaoProcessoAdministrativo.setCorrespondencia(correspondencia);

        notificacaoProcessoAdministrativo
                .setCorpoNotificacao(
                        getApoioService().getNotificacaoAssinaturaTextoByAtivoETipo(idTipoCorpoTexto)
                );

        PAEnderecoAlternativo endereco = preencherEnderecoNotificacao(em, notificacaoProcessoAdministrativo, pa.getCpf());

        notificacaoProcessoAdministrativo.setNumeroNotificacao(getNumeroNotificacao());

        notificacaoProcessoAdministrativo.setByteImagemCodigoBarraCep(
                DetranCodigoBarraHelper.codigoBarra2de5(
                        DetranStringUtil.preencherEspaco(
                                endereco.getCep().toString(), 8, DetranStringUtil.TipoDadoEnum.NUMERICO)));

        notificacaoProcessoAdministrativo
                .setObjetoCorreio(PREFIXO_OBJETO.concat(notificacaoProcessoAdministrativo.getNumeroNotificacao().toString()));

        notificacaoProcessoAdministrativo
                .setByteImagemCodigoBarraCorreio(
                        DetranCodigoBarraHelper.codigoBarra3de9(notificacaoProcessoAdministrativo.getObjetoCorreio()));

        notificacaoProcessoAdministrativo.setTipoNotificacaoProcesso(tipoNotificacao);

        notificacaoProcessoAdministrativo.setDataPostagem(getApoioService().getProximoDiaUtilFeriadoEstadualNacional());

        notificacaoProcessoAdministrativo.setAtivo(AtivoEnum.ATIVO);

        return (NotificacaoProcessoAdministrativo) genericRepository.insert(em, notificacaoProcessoAdministrativo);
    }

    /**
     * @param notificacaoProcessoAdministrativo
     * @param cpf
     * @throws DatabaseException
     * @throws AppException
     */
    private PAEnderecoAlternativo preencherEnderecoNotificacao(EntityManager em,
            NotificacaoProcessoAdministrativo notificacaoProcessoAdministrativo, String cpf) throws DatabaseException, AppException {

        PAEnderecoAlternativoRepositorio enderecoRepo = new PAEnderecoAlternativoRepositorio();

        PAEnderecoAlternativo enderecoAlternativo = enderecoRepo.getEnderecoAtivoPorPA(em, notificacaoProcessoAdministrativo.getProcessoAdministrativo().getId());

        if (enderecoAlternativo == null || TipoEnvolvidoPAEnum.CONDUTOR_SISTEMA.equals(enderecoAlternativo.getTipoEnvolvido())) {
            if (enderecoAlternativo != null && TipoEnvolvidoPAEnum.CONDUTOR_SISTEMA.equals(enderecoAlternativo.getTipoEnvolvido())) {

                enderecoAlternativo.setAtivo(AtivoEnum.DESATIVADO);
                enderecoRepo.update(em, enderecoAlternativo);
            }

            enderecoAlternativo = preencherEnderecoAlternativo(em, cpf, notificacaoProcessoAdministrativo);
        }

        notificacaoProcessoAdministrativo.setEndereco(enderecoRepo.insertOrUpdate(em, enderecoAlternativo));

        return enderecoAlternativo;
    }

    private PAEnderecoAlternativo preencherEnderecoAlternativo(EntityManager em,
            String cpf,
            NotificacaoProcessoAdministrativo notificacaoProcessoAdministrativo) throws DatabaseException, AppException, NumberFormatException {

        AEMNPP08 aemnpp08 = new AEMNPP08BO().executarIntegracaoAEMNPP08(cpf);
        if (aemnpp08 == null) {
            DetranWebUtils.applicationMessageException("Não foi possível obter o endereço para gerar notificação");
        }

        PAEnderecoAlternativo enderecoAlternativo = new PAEnderecoAlternativo();

        enderecoAlternativo.setNumero(" ");

        ArrayList<String> partesComplemento = new ArrayList(DetranCollectionUtil.montaLista(aemnpp08.getComplemento().split(" ")));
        if (!DetranCollectionUtil.ehNuloOuVazio(partesComplemento) && DetranStringUtil.isDigits(partesComplemento.get(0))) {
            enderecoAlternativo.setNumero(partesComplemento.get(0));
            partesComplemento.remove(0);
        }
        enderecoAlternativo.setComplemento(String.join(" ", partesComplemento));

        enderecoAlternativo.setBairro(aemnpp08.getBairro());
        enderecoAlternativo.setCep(Integer.parseInt(aemnpp08.getCep()));
        enderecoAlternativo.setLogradouro(aemnpp08.getLogradouro());

        PAMunicipioWrapper municipio
                = new PAApoioRepositorio()
                        .getMunicipioPeloCodigo(
                                em, Integer.parseInt(aemnpp08.getMunicipio())
                        );

        enderecoAlternativo.setMunicipio(municipio.getId());
        enderecoAlternativo.setProcessoAdministrativo(notificacaoProcessoAdministrativo.getProcessoAdministrativo());
        enderecoAlternativo.setTipoEnvolvido(TipoEnvolvidoPAEnum.CONDUTOR_SISTEMA);
        enderecoAlternativo.setAtivo(AtivoEnum.ATIVO);

        return enderecoAlternativo;
    }

    /**
     * @param em
     * @param urlReportBirt
     * @param notificacaoPa
     * @throws AppException
     */
    public void gerarArquivoLab(EntityManager em,
            String urlReportBirt,
            NotificacaoProcessoAdministrativo notificacaoPa) throws AppException {

        PATipoCorpoAndamento tipoCorpoAndamento
                = new PATipoCorpoAndamentoRepositorio()
                        .getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracao(
                                em,
                                notificacaoPa.getTipoNotificacaoProcesso(),
                                notificacaoPa.getProcessoAdministrativo().getOrigemApoio());

        ArquivoPA arquivoPa = gerarArquivoNotificacao(em, notificacaoPa, tipoCorpoAndamento.getTipoCorpoTexto(), urlReportBirt);

        notificacaoPa.setArquivo(arquivoPa);

        new NotificacaoProcessoAdministrativoRepositorio().update(em, notificacaoPa);

    }

    /**
     * @param em
     * @param urlReportBirt
     * @param notificacaoPa
     * @throws AppException
     */
    public void gerarArquivoNotificacaoJARI(EntityManager em,
            String urlReportBirt,
            NotificacaoProcessoAdministrativo notificacaoPa) throws AppException {

        ResultadoRecurso resultado
                = new ResultadoRecursoRepositorio().
                        getResultadoRecursoPorProcessoAdministrativoESituacaoEDestino(em,
                                notificacaoPa.getProcessoAdministrativo().getId(),
                                SituacaoRecursoEnum.JULGADO,
                                OrigemDestinoEnum.JARI);

        Notificacao corpoTexto = getCorpoTextoNotificacaoJARI(em, resultado);

        ArquivoPA arquivoPa = gerarArquivoNotificacao(em, notificacaoPa, corpoTexto.getTipoCorpoTexto().getId(), urlReportBirt);

        notificacaoPa.setArquivo(arquivoPa);

        new NotificacaoProcessoAdministrativoRepositorio().update(em, notificacaoPa);

    }

    private ArquivoPA gerarArquivoNotificacao(EntityManager em,
            NotificacaoProcessoAdministrativo notificacaoPa,
            Long idTipoCorpoTexto,
            String urlReportBirt) throws DatabaseException, AppException {

        ArquivoPA arquivoPa = new ArquivoPA();

        TipoCorpoTexto tipoCorpoTexto
                = (TipoCorpoTexto) getApoioService().getTipoCorpoTexto(idTipoCorpoTexto);

        Map<String, String> parametros = new HashMap();

        if (TipoFasePaEnum.DESENTRANHAMENTO.equals(notificacaoPa.getTipoNotificacaoProcesso())) {

            PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, notificacaoPa.getProcessoAdministrativo().getId());

            parametros.put("dataInicio", Utils.formatDate(penalidade.getDataInicioPenalidade(), "yyyy-MM-dd"));
            parametros.put("dataFim", Utils.formatDate(penalidade.getDataFimPenalidade(), "yyyy-MM-dd"));
        }

        parametros.put("numeroNotificacao", null != notificacaoPa.getNumeroNotificacao()
                ? notificacaoPa.getNumeroNotificacao().toString()
                : null);

        String urlRelatorio
                = DetranReportsUtil.getReportParamsBirtUrl(
                        "notificacao_" + notificacaoPa.getTipoNotificacaoProcesso().name().toLowerCase(),
                        FormatoRelatorioEnum.PDF.getRptFormat(),
                        parametros,
                        "relatorios/processoadministrativo/notificacao/");

        arquivoPa.setByteArquivo(DetranHTTPUtil.download(urlReportBirt + urlRelatorio));

        TipoArquivo tipoArquivo
                = (TipoArquivo) getApoioService()
                        .getTipoArquivoPorCodigoEModulo(
                                TIPO_NOTIFICACAO_INDIVIDUAL,
                                ModuloTipoArquivoEnum.PA);

        arquivoPa.setTipoArquivo(tipoArquivo.getId());
        arquivoPa.setDescricao(tipoCorpoTexto.getDescricao());
        arquivoPa.setExtensao(TipoExtensaoArquivoEnum.PDF);
        arquivoPa.setTabela("TB_TCX_NOTIFICACAO_PROCESSO_ADM");
        arquivoPa.setIdTabela(notificacaoPa.getId());
        arquivoPa.setAtivo(AtivoEnum.ATIVO);

        new ArquivoPARepositorio().insert(em, arquivoPa);

        return arquivoPa;
    }

    /**
     * Gracar a Correspondencia do ProcessoAdministrativo para a
     * NotificacaoProcessoAdministrativo.
     *
     * @param em
     * @param pa
     * @return
     * @throws AppException
     */
    public Correspondencia gravarCorrespondencia(EntityManager em,
            ProcessoAdministrativo pa,
            TipoFasePaEnum fase) throws AppException {

        Correspondencia correspondencia = new Correspondencia();

        correspondencia.setSetorTipoCorrespondencia(getSetorTipoCorrespondencia(em));
        correspondencia.setEspecie(getEspecieNotificacaoPA(em));
        correspondencia.setMeioEnvio(getMeioEnvio(em));
        correspondencia.setQteEspecie(QTE_ESPECIE);
        correspondencia.setTipoOrigem(ORIGEM_DETRAN_EXTERNO);
        correspondencia.setFormaEnvio(REMESSA_CORREIOS);
        correspondencia.setObservacao("Notificação" + fase.getTipoFasePaExterna() + " Processo N. " + pa.getNumeroProcesso());
        correspondencia.setSituacao(ENVIADA);
        correspondencia.setUsuarioInclusaoCorrespondencia("BPMS");
        correspondencia.setDataInclusaoCorrespondencia(new Date());
        correspondencia.setAtivo(AtivoEnum.ATIVO);
        correspondencia.setRemetente(SETOR);
        correspondencia.setDestinatario(getApoioService().getNomeDoAtendimento(pa.getAtendimento()));

        return (Correspondencia) genericRepository.insert(em, correspondencia);

    }

    /**
     *
     * @return @throws AppException
     */
    private String getNumeroNotificacao() throws AppException {

        Long numeroNotificacao
                = getStoredProcedureService().
                        obterSequencial(
                                getJSPAObtemSequencialProcessoAdministrativo(TipoNumeroSequencialConstante.NOTIFICACAO_PA)
                        );

        if (numeroNotificacao == null) {
            DetranWebUtils.applicationMessageException("Número Processo inválido.");
        }

        return numeroNotificacao.toString();
    }

    /**
     *
     * @param em
     * @return
     * @throws DatabaseException
     */
    private CorrespondenciaEspecie getEspecieNotificacaoPA(EntityManager em) throws DatabaseException {
        return (CorrespondenciaEspecie) genericRepository
                .getNamedQuery(em,
                        "CorrespondenciaEspecie.getCorrespondenciaEspeciePorDescricao", "Notificações PA", AtivoEnum.ATIVO);
    }

    /**
     *
     * @param em
     * @return
     * @throws DatabaseException
     */
    private CorrespondenciaMeioEnvio getMeioEnvio(EntityManager em) throws DatabaseException {
        return (CorrespondenciaMeioEnvio) genericRepository
                .getNamedQuery(em,
                        "CorrespondenciaMeioEnvio.getCorrespondenciaMeioEnvioPorDescricao", "Envelope", AtivoEnum.ATIVO);
    }

    /**
     * @param em
     * @param entrada
     * @param notificacao
     * @throws DatabaseException
     */
    public void gravarNotificacaoComplemento(EntityManager em,
            NotificaProcessoAdministrativoWrapper entrada,
            NotificacaoProcessoAdministrativo notificacao) throws AppException {

        NotificacaoComplemento notificacaoComplemento = new NotificacaoComplemento();

        notificacaoComplemento.setNotificacao(notificacao);

        notificacaoComplemento.setNumeroPortaria(entrada.getNumeroPortaria());
        notificacaoComplemento.setData(entrada.getDataPublicacaoPortaria());
        notificacaoComplemento.setAtivo(AtivoEnum.ATIVO);

        PAComplemento complemento
                = new PAComplementoRepositorio()
                        .getPAComplementoPorParametroEAtivo(em,
                                notificacao.getProcessoAdministrativo(),
                                PAParametroEnum.TEMPO_PENALIDADE);

        notificacaoComplemento.setTempoPenalidade(complemento != null ? complemento.getValor() : null);

        new NotificacaoComplementoRepositorio().insert(em, notificacaoComplemento);
    }

    /**
     * Retornar notificações da instauração.
     *
     * @param em
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    public List getNotificados(EntityManager em) throws AppException {

        List<NotificadosWrapper> listaJson = new ArrayList();

        List<Integer> andamentosNotificados
                = DetranCollectionUtil
                        .montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.CONFIRMAR_NOTIFICACAO_INSTAURACAO,
                                PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.CONFIRMAR_NOTIFICACAO_PENALIZACAO,
                                PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.CONFIRMAR_NOTIFICACAO_ENTREGA_CNH,
                                PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.CONFIRMAR_NOTIFICACAO_ACOLHIMENTO,
                                PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.CONFIRMAR_NOTIFICACAO_DESENTRANHAMENTO,
                                PAAndamentoProcessoConstante.NOTIFICACAO_JARI.CONFIRMAR_NOTIFICACAO_JARI,
                                PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.CONFIRMAR_NOTIFICACAO_CURSO_EXAME
                        );

        List<NotificacaoProcessoAdministrativo> notificados
                = new NotificacaoProcessoAdministrativoRepositorio()
                        .getNotificadosPorAndamentoESituacao(em, andamentosNotificados);

        if (!DetranCollectionUtil.ehNuloOuVazio(notificados)) {

            for (NotificacaoProcessoAdministrativo notificado : notificados) {

                listaJson
                        .add(
                                new NotificadosWrapper(
                                        notificado.getProcessoAdministrativo().getNumeroProcesso(),
                                        notificado.getNumeroNotificacao(),
                                        notificado.getObjetoCorreio(),
                                        notificado.getTipoNotificacaoProcesso(),
                                        notificado.getDataPrazoLimite(),
                                        notificado.getLote() != null ? notificado.getLote().getNome() : null,
                                        notificado.getDataNotificacao()
                                )
                        );
            }
        }

        return listaJson;
    }

    /**
     *
     * @param em
     * @param processoAdministrativo
     * @param dataInicio
     * @throws AppException
     */
    private void gravaPAPenalidadeProcesso(
            EntityManager em, PAPenalidadeProcesso paPenalidadeProcessoAtiva,
            ProcessoAdministrativo processoAdministrativo, Date dataInicio) throws AppException {

        PAPenalidadeProcesso paPenalidadeProcesso = new PAPenalidadeProcesso();

        paPenalidadeProcesso.setAtivo(AtivoEnum.ATIVO);
        paPenalidadeProcesso.setDataCadastro(Calendar.getInstance().getTime());
        paPenalidadeProcesso.setProcessoAdministrativo(processoAdministrativo);
        paPenalidadeProcesso.setUsuario(paPenalidadeProcessoAtiva.getUsuario());
        paPenalidadeProcesso.setUnidadePenal(paPenalidadeProcessoAtiva.getUnidadePenal());

        paPenalidadeProcesso.setDataInicioPenalidade(dataInicio);

        PAComplemento paComplemento
                = new PAComplementoRepositorio()
                        .getPAComplementoPorParametroEAtivo(em, processoAdministrativo, PAParametroEnum.TEMPO_PENALIDADE);

        if (paComplemento != null && !DetranStringUtil.ehBrancoOuNulo(paComplemento.getValor())) {
            paPenalidadeProcesso.setDataFimPenalidade(Utils.addMonth(dataInicio, Integer.valueOf(paComplemento.getValor())));
            paPenalidadeProcesso.setValor(Integer.valueOf(paComplemento.getValor()));
        }

        new PAPenalidadeProcessoRepositorio().insert(em, paPenalidadeProcesso);
    }

    private void validarAndamentoTipoNotificacao(EntityManager em, ProcessoAdministrativo processoAdministrativo, TipoFasePaEnum tipo) throws AppException {

        PAOcorrenciaStatus ocorrenciaAtual
                = new PAOcorrenciaStatusRepositorio()
                        .getPAOcorrenciaStatusAtiva(em, processoAdministrativo.getId());

        Integer codigoAndamento
                = new PATipoCorpoAndamentoRepositorio().
                        getCodigoAndamentoPorTipoNotificacaoEOrigemPA(
                                em,
                                tipo,
                                processoAdministrativo.getOrigemApoio());

        if (ocorrenciaAtual == null
                || !codigoAndamento.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {

            DetranWebUtils.applicationMessageException("O andamento do Processo Administrativo é diferente do Andamento informado.");
        }
    }

    public List getPAComPrazoNotificacaoTerminado(EntityManager em) throws AppException {
        List<NotificadosWrapper> listaJson = new ArrayList();

        List<Integer> andamentosNotificados
                = DetranCollectionUtil
                        .montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_INSTAURACAO,
                                PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_PENALIZACAO,
                                PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_ENTREGA_CNH,
                                PAAndamentoProcessoConstante.NOTIFICACAO_JARI.CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_JARI,
                                PAAndamentoProcessoConstante.NOTIFICACAO_CETRAN.CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_CETRAN
                        );

        List<NotificacaoProcessoAdministrativo> notificados
                = new NotificacaoProcessoAdministrativoRepositorio()
                        .getNotificadosPorAndamentoESituacao(em, andamentosNotificados);

        if (!DetranCollectionUtil.ehNuloOuVazio(notificados)) {

            notificados.forEach((notificado) -> {
                listaJson
                        .add(
                                new NotificadosWrapper(
                                        notificado.getProcessoAdministrativo().getNumeroProcesso(),
                                        notificado.getNumeroNotificacao(),
                                        notificado.getTipoNotificacaoProcesso(),
                                        notificado.getDataPrazoLimite()
                                )
                        );
            });
        }

        return listaJson;
    }

    /**
     * Método usado para validar quando um prazo de notificação encerrou e o PA
     * pode ir para o próximo andamento.
     *
     * @param em
     * @param andamentoEspecificoWrapper
     * @param tipo
     * @throws AppException
     * @throws DatabaseException
     */
    public void validarNotificacaoDataLimiteExpirada(EntityManager em, ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper, TipoFasePaEnum tipo) throws AppException, DatabaseException {
        NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoRepositorio().
                        getNotificacaoPorNumeroProcessoETipo(em,
                                andamentoEspecificoWrapper.getProcessoAdministrativo().getNumeroProcesso(),
                                tipo
                        );
        if (notificacao == null) {
            DetranWebUtils.applicationMessageException("Não foi possível recuperar a notificação.");
        }

        Date dataAtual = Calendar.getInstance().getTime();
        
        if(notificacao.getTipoNotificacaoProcesso().equals(TipoFasePaEnum.JARI)){
            ResultadoRecurso resultado
                = new ResultadoRecursoRepositorio().
                        getResultadoRecursoPorProcessoAdministrativoESituacaoEDestino(em,
                                notificacao.getProcessoAdministrativo().getId(),
                                SituacaoRecursoEnum.JULGADO,
                                OrigemDestinoEnum.JARI);
            if(resultado!=null && resultado.getResultado().equals(ResultadoRecursoEnum.PROVIDO)){
                return;
            }
        }
        
        EditalProcessoAdministrativo edital = new EditalProcessoAdministrativoRepositorio().getEditalPorNotificacao(em, notificacao.getId());
        
        if(edital.getTerminoPrazoEdital().after(dataAtual)){
            DetranWebUtils.applicationMessageException("Processo dentro do prazo de notificação de edital.");
        }
        
    }

    private Long getSetorTipoCorrespondencia(EntityManager em) throws AppException {

        Long idCorrespondenciaTipo = new CorrespondenciaTipoRepositorio().getIdCorrespondenciaTipoPorCodigoAtivo(em, NOTIFICACAO_PA);

        if (null == idCorrespondenciaTipo) {
            DetranWebUtils.applicationMessageException("Erro no metadados! Tabela TB_TAA_CORRESPONDENCIA_TIPO - CODIGO 2");
        }

        Long idSetorTipoCorrespondencia = getAdministrativoService().getIdSetorTipoCorrespondenciaPorSetor(SETOR, idCorrespondenciaTipo);
        if (null == idSetorTipoCorrespondencia) {
            DetranWebUtils.applicationMessageException("Erro no metadados! "
                    + "TB_TBE_SETOR_TIPO_CORRESPONDENCIA -"
                    + "Envolve - TB_TAA_CORRESPONDENCIA_TIPO - CODIGO 2"
                    + " E TB_TBC_SETOR_CORRESPONDENCIA - SIGLA - " + SETOR);
        }

        return idSetorTipoCorrespondencia;
    }

    /**
     *
     * @param em
     * @throws AppException
     */
    public void checaQuantidadeDiariaLimiteEmissaoNotificacao(EntityManager em) throws AppException {

        BigDecimal parametroQuantidade
                = new PAParametrizacaoRepositorio().getQuantidadeLimiteDiarioEmissaoNotificacao(em);

        Long quantidadeEmissaoNotificacaoDiaria
                = new NotificacaoProcessoAdministrativoRepositorio().getQuantidadeNotificacaoPorDataNotificacao(em, Calendar.getInstance().getTime());

        if (parametroQuantidade != null && quantidadeEmissaoNotificacaoDiaria != null) {

            if (quantidadeEmissaoNotificacaoDiaria >= parametroQuantidade.longValue()) {
                DetranWebUtils.applicationMessageException("Quantidade Limite Diário de Emissão atingido.");
            }
        }
    }
}
