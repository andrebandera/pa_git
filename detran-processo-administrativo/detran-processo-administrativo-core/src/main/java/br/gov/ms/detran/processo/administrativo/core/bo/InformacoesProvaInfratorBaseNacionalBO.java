package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.adm.ResultadoLaudoSituacao;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.Municipio;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.core.iface.servico.hab.IHabilitacaoService;
import br.gov.ms.detran.integracao.comum.wrapper.AEBNH011;
import br.gov.ms.detran.integracao.comum.wrapper.AEBNH011A;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEBNH011BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoProvaPA;
import java.util.Date;
import javax.persistence.EntityManager;

public class InformacoesProvaInfratorBaseNacionalBO {

    private static final Logger LOG = Logger.getLogger(InformacoesProvaInfratorBaseNacionalBO.class);

    IApoioService apoioService;

    /**
     * @return
     */
    IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    IHabilitacaoService habilitacaoService;

    /**
     *
     * @return
     */
    IHabilitacaoService getHabilitacaoService() {
        if (habilitacaoService == null) {
            habilitacaoService = (IHabilitacaoService) JNDIUtil.lookup("ejb/HabilitacaoService");
        }
        return habilitacaoService;
    }

    /**
     * Obter informações da prova e realizar a gravação da TDY.
     *
     * @param em
     * @param numeroProcessoMascarado
     * @param usuarioLogado
     * @throws AppException
     */
    public void obterInformacoesProvaInfrator(EntityManager em,
            String numeroProcessoMascarado,
            DetranUserDetailsWrapper usuarioLogado) throws AppException {

        ProcessoAdministrativo pa = new ProcessoAdministrativoRepositorio().getProcessoAdministrativoPorNumeroProcessoAtivo(em, new NumeroAnoAdapter().unmarshal(numeroProcessoMascarado));

        /**
         * Buscar informações da prova na nossa base WEB
         */
        ResultadoProvaPA resultadoProvaPA = obterInformacoesProvaInfratorBaseLocalWeb(em, pa);

        /**
         * Buscar informações da prova na BASE NACIONAL
         */
        if (null == resultadoProvaPA || null == resultadoProvaPA.getResultadoLaudoSituacao()) {

            resultadoProvaPA = obterInformacoesProvaInfratorBaseNacional(em, pa, usuarioLogado.getOperador().getUsuario().getId());

        }

        new ResultadoProvaPABO().gravacaoParaProcessosAguardandoRealizacaoCurso(em, pa.getNumeroDetran(), resultadoProvaPA);
        new ResultadoProvaPABO().gravacaoParaProcessosComAndamentoDiferenteAguardandoCurso(em, pa.getNumeroDetran(), resultadoProvaPA);

    }

    /**
     * Pesquisar as informações da prova na nossa base WEB.
     *
     * @param em
     * @param pa
     * @return
     */
    public ResultadoProvaPA obterInformacoesProvaInfratorBaseLocalWeb(EntityManager em,
            ProcessoAdministrativo pa) throws AppException {

        ResultadoProvaPA resultadoProvaPA = new ResultadoProvaPA();

        ResultadoLaudoSituacao resultadoLaudoSituacao
                = (ResultadoLaudoSituacao) getApoioService().getResultadoLaudoSituacaoParaInformacoesNoProcessoAdministrativo(pa.getNumeroDetran());

        if (null != resultadoLaudoSituacao) {
            resultadoProvaPA.setResultadoLaudoSituacao(resultadoLaudoSituacao.getId());
            resultadoProvaPA.setDataProva(resultadoLaudoSituacao.getExame().getExame().getDataExame());
        }

        return resultadoProvaPA;
    }

    /**
     * Pesquisar as informações da prova na base nacional.
     *
     * @param em
     * @param pa
     * @return
     */
    public ResultadoProvaPA obterInformacoesProvaInfratorBaseNacional(EntityManager em,
            ProcessoAdministrativo pa,
            Long idUsuario) throws AppException {

        ResultadoProvaPA resultadoProvaPA = new ResultadoProvaPA();

        AEBNH011 aebnh011 = new AEBNH011BO().executarIntegracaoAEBNH011(pa.getCpf());

        if (null != aebnh011 && !DetranCollectionUtil.ehNuloOuVazio(aebnh011.getExames())) {

            for (Object exame : aebnh011.getExames()) {

                AEBNH011A aebnh011a = (AEBNH011A) exame;

                if (null != aebnh011a.getMuniciprioProva()) {
                    Municipio municipio = (Municipio) getApoioService().getMunicipioPeloCodigo(aebnh011a.getMuniciprioProva());

                    resultadoProvaPA.setMunicipio(null != municipio ? municipio.getId() : null);
                }

                resultadoProvaPA.setDataProva(aebnh011a.getDataProva());
                resultadoProvaPA.setTipoResultadoExame(getApoioService().getTipoResultadoExameApto());
                resultadoProvaPA.setUsuario(idUsuario);
                resultadoProvaPA.setDataCadastro(new Date());
            }

        } else {
            DetranWebUtils.applicationMessageException("infracoesprovainfratorbasenacional.validar.naoexisteinfo.exception");
        }

        return resultadoProvaPA;
    }
}
