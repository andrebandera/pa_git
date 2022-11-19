package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP88;
import br.gov.ms.detran.processo.administrativo.constantes.StatusPontuacaoConstante;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP88BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.DadosInfracaoPADRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAApoioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Lillydi
 */
public class RegrasGeralInstauracaoInfracaoBO {
    
    private static final Logger LOG = Logger.getLogger(RegrasGeralInstauracaoInfracaoBO.class);

    private static final Integer PERIODO_PERMISSIONADO              = 364;
    private IPAControleFalhaService iFalhaService;
    
     public IPAControleFalhaService getFalhaService() {

        if (iFalhaService == null) {
            iFalhaService = (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }

        return iFalhaService;
    }

    /**
     * 
     * @param infracao
     * @param valorReferenciaMes
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public void validarRegraInfracaoPrescrita(DadosInfracaoPAD infracao, Integer valorReferenciaMes) throws RegraNegocioException, AppException {

        if (validarDataInfracaoPrescrita(infracao.getDataInfracao(), valorReferenciaMes)) {
            
            throw new RegraNegocioException("Infração Prescrita. "
                    + "" + infracao.getIsn()
                    + " " + infracao.getDataInfracao()
                    + " | ValorReferenciaMes" + valorReferenciaMes);
        }

    }
    
    /**
     * 
     * @param infracao
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public void validarRegraInfracaoRevogada(DadosInfracaoPAD infracao)throws RegraNegocioException, AppException{
        List<String> infracoesRevogadas = DetranCollectionUtil.montaLista("70302");
        
        if(StatusPontuacaoConstante.ATIVO.equals(infracao.getStatusPontuacao())
                && infracoesRevogadas.contains(infracao.getInfracaoCodigo())){
            throw new RegraNegocioException("Infração Revogada. "
                    + "" + infracao.getIsn()
                    + " " + infracao.getDataInfracao()
                    + " | Infração - " + infracao.getInfracaoCodigo());
        }
    }

    /**
     * 
     * @param em
     * @param infracao
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public void validarRegraNaoPodeExistirInfracaoEmProcessoAdministrativo(
        EntityManager em, DadosInfracaoPAD infracao) throws RegraNegocioException, AppException {
        
        Long idAutuadorPeloCodigo = new PAApoioRepositorio().getOrgaoAutuadorIdPeloCodigo(em, infracao.getAutuador());

        List<ProcessoAdministrativoInfracao> lista = 
            new ProcessoAdministrativoInfracaoRepositorio()
                .getInfracaoPorInfracaoNumeroAutoOrgaoAutuador(
                    em,
                    infracao.getInfracaoCodigo(),
                    infracao.getAuto(),
                    idAutuadorPeloCodigo
                );

        if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {
            
            String falha = "Infração já existente em um processo administrativo. "
                    + "" + infracao.getIsn()
                    + "" + infracao.getCpfCondutor().getCpf()
                    + "" + infracao.getAuto()
                    + "" + infracao.getAutuador();
            
            getFalhaService().gravarFalhaEspecifica(infracao.getCpfCondutor().getCpf(), falha, "INSTAURACAO_INFRACAO_EXISTENTE");
            
            throw new RegraNegocioException(falha);
        }
    }

    /**
     * 
     * @param infracao
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public void validarRegraInfracaoCometidaAposPeriodoPermissionado(
            DadosInfracaoPAD infracao) throws RegraNegocioException, AppException {

        Date dataValidadePeriodoPermissionado = Utils.addDayMonth(infracao.getCpfCondutor().getDataPrimeiraHabilitacao(), PERIODO_PERMISSIONADO);

        if (!infracao.getDataInfracao().after(dataValidadePeriodoPermissionado)) {
            
            throw new RegraNegocioException("Infração cometida após período permissionado. "
                    + "" + infracao.getIsn()
                    + "" + infracao.getCpfCondutor().getCpf()
                    + "" + infracao.getAuto()
                    + "" + infracao.getAutuador());
        }
    }

    /**
     * 
     * @param dataInfracao
     * @param valorReferenciaMes
     * @return
     * @throws AppException 
     */
    private Boolean validarDataInfracaoPrescrita(Date dataInfracao, Integer valorReferenciaMes) throws AppException {
        
        Date dataAtual = new Date();
        
        if (dataInfracao == null || valorReferenciaMes == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        return Utils.getDataComHoraInicial(dataAtual).after(Utils.addMonth(dataInfracao, valorReferenciaMes));
    }

    /**
     * 
     * @param infracao
     * @param apoio
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public void regraStatusPontuacao(DadosInfracaoPAD infracao, ApoioOrigemInstauracao apoio) throws RegraNegocioException, AppException {

        if (infracao == null) {
            throw new RegraNegocioException("Infração inválida.");
        }
        
        if(DetranStringUtil.ehBrancoOuNulo(apoio.getStatusInfracao())){
            DetranWebUtils.applicationMessageException("Não existe status definido para a regra {0}", "", apoio.getRegra().name());
        }
        
        List<String> status = DetranCollectionUtil.montaLista(apoio.getStatusInfracao().split(";"));
        
        if (!status.contains(infracao.getStatusPontuacao())) {
            throw new RegraNegocioException("Infração status diferente do cadastrado na TDH.");
        }
    }

    /**
     * @param em
     * @param item
     * @param wrapper
     * @throws RegraNegocioException
     * @throws AppException
     */
    public void validarCodigoAcaoInstauracao(EntityManager em, DadosInfracaoPAD item, InstaurarProcessoAdministrativoWrapper wrapper) throws RegraNegocioException, AppException {

        boolean acaoInstauracaoDaInfracao = 
            new PAInfracaoRepositorio().hasAcaoInstauracaoDaInfracao(em, 
                item.getInfracaoCodigo(),
                wrapper.getApoioOrigemInstauracao().getAmparoLegal(),
                wrapper.getApoioOrigemInstauracao().getResultadoAcao().ordinal(),
                wrapper.getApoioOrigemInstauracao().getAcaoSistema().ordinal(),
                item.getDataInfracao()
            );

        if (!acaoInstauracaoDaInfracao) {
            throw new RegraNegocioException("Código ação instauração não é " + wrapper.getApoioOrigemInstauracao().getResultadoAcao()
                    + " ISN:" + item.getIsn()
                    + " CPF:" + item.getCpfCondutor().getCpf()
                    + " AUTO:" + item.getAuto()
                    + " AUTUADOR:" + item.getAutuador());
        }
    }

    /**
     * 
     * @param em
     * @param infracao
     * @param origensValidas
     * @throws RegraNegocioException
     * @throws AppException 
     */
    private void validarRegraOrigemInfracao(DadosInfracaoPAD infracao, List<String> origensValidas) throws RegraNegocioException {
        
        if (!origensValidas.contains(infracao.getOrigemInformacaoPontuacao().toString())) {

            throw new RegraNegocioException("Infração com Origem diferente da cadastrada na TDH. "
                                            + " " + infracao.getIsn()
                                            + " " + infracao.getCpfCondutor().getCpf()
                                            + " " + infracao.getAuto()
                                            + " " + infracao.getAutuador());
        }
    }
    
    /**
     * 
     * @param infracao
     * @param apoio
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public void validarRegraOrigemInfracao(DadosInfracaoPAD infracao, ApoioOrigemInstauracao apoio) throws RegraNegocioException, AppException {

        if(DetranStringUtil.ehBrancoOuNulo(apoio.getOrigemInformacaoPontuacao())){
            DetranWebUtils.applicationMessageException("Não existe origem informação pontuação definida para a regra {0}", "", apoio.getRegra().name());
        }
        List<String> origens = DetranCollectionUtil.montaLista(apoio.getOrigemInformacaoPontuacao().split(";"));
        validarRegraOrigemInfracao(infracao, origens);
    }
    
    /**
     * 
     * @param em
     * @param dadosInfracaoPAD
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public void regraInfracaoValida(EntityManager em, DadosInfracaoPAD dadosInfracaoPAD) throws RegraNegocioException, AppException {

        if (DetranStringUtil.ehBrancoOuNulo(dadosInfracaoPAD.getInfracaoCodigo())) {
            throw new RegraNegocioException("Código infração inválido.");
        }

        if (new PAInfracaoRepositorio().naoExisteAcaoInfracaoPenalidadeAtivoPorCodigoInfracao(em, dadosInfracaoPAD.getInfracaoCodigo())) {
            throw new RegraNegocioException("A infração é inválida.");
        }
    }
    
    /**
     * @param em
     * @param dadosCondutorPAD
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public void validarCondutorSemProcessoAdministrativo(EntityManager em, DadosCondutorPAD dadosCondutorPAD) throws RegraNegocioException, AppException {
        List processos = 
                new ProcessoAdministrativoRepositorio()
                        .getProcessosAdministrativosSuspensaoAtivos(em, dadosCondutorPAD.getCpf());
        
        if (!DetranCollectionUtil.ehNuloOuVazio(processos)) {
            throw new RegraNegocioException("Já existe Processo Administrativo para o condutor.");
        }
    }
    
    /**
     * @param em
     * @param infracao
     * @param processos
     * @return 
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public ProcessoAdministrativo validarMesmoArtigoInfracaoPeriodo1Ano(EntityManager em, DadosInfracaoPAD infracao, List processos) throws RegraNegocioException, AppException {
        
        List<Long> idsPA = Lists.transform(processos, new Function<ProcessoAdministrativoWrapper, Long>() {
            @Override
            public Long apply(ProcessoAdministrativoWrapper f) {
                return f.getId();
            }
        });
        
        List<ProcessoAdministrativoInfracao> infracoes = 
            new ProcessoAdministrativoInfracaoRepositorio()
                    .getInfracaoPAReincidenteMesmaInfracao(em, infracao.getInfracaoCodigo(), idsPA);
        
        return validarInfracaoNoIntervalo1Ano(infracao, infracoes);
    }
    
    public ProcessoAdministrativo validarMesmoCodigoInfracaoPeriodo1Ano(
        EntityManager em, DadosInfracaoPAD infracao, List processos) throws RegraNegocioException, AppException {
        
        List<Long> idsPA = Lists.transform(processos, new Function<ProcessoAdministrativoWrapper, Long>() {
            @Override
            public Long apply(ProcessoAdministrativoWrapper f) {
                return f.getId();
            }
        });
        
        List<ProcessoAdministrativoInfracao> infracoes = 
            new ProcessoAdministrativoInfracaoRepositorio()
                    .getInfracaoPAReincidenteMesmaInfracao(em, infracao.getInfracaoCodigo(), idsPA);
        
        return validarInfracaoNoIntervalo1Ano(infracao, infracoes);
    }
    
    /**
     * 
     * @param em
     * @param infracao
     * @return 
     * @throws AppException 
     */
    public DadosInfracaoPAD validaAMesmaInfracaoFoiRealizadaEm1AnoOuMenos(EntityManager em, DadosInfracaoPAD infracao, Integer valorReferencia) throws AppException {
        
        List<DadosInfracaoPAD> dadosInfracoes 
            = new DadosInfracaoPADRepositorio()
                .getAMesmaInfracaoFoiRealizadaEm1AnoOuMenos(
                    em, 
                    infracao.getCpfCondutor().getCpf(), 
                    infracao.getInfracaoCodigo(), 
                    infracao.getDataInfracao(),
                    infracao.getIsn()
                );
        dadosInfracoes = validarInfracoes(em, dadosInfracoes, valorReferencia);
        
        return DetranCollectionUtil.ehNuloOuVazio(dadosInfracoes)? null: dadosInfracoes.get(0);
    }
    
    /**
     * @param em
     * @param infracao
     * @param processos
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public void validarMesmoArtigoInfracaoAposPeriodo1Ano(EntityManager em, DadosInfracaoPAD infracao, List processos) throws RegraNegocioException, AppException {
        
        List<Long> idsPA = Lists.transform(processos, new Function<ProcessoAdministrativoWrapper, Long>() {
            @Override
            public Long apply(ProcessoAdministrativoWrapper f) {
                return f.getId();
            }
        });
        
        List<ProcessoAdministrativoInfracao> infracoes = 
            new ProcessoAdministrativoInfracaoRepositorio()
                    .getInfracaoPAReincidenteMesmaInfracao(em, infracao.getInfracaoCodigo(), idsPA);
        
         if(DetranCollectionUtil.ehNuloOuVazio(infracoes)){
            throw new RegraNegocioException("Não há infração com mesmo artigo para esta infração.");
        }
        
        validarInfracaoMesmoArtigoForaIntervalo(infracao, infracoes);
    }
    
    /**
     * @param infracao
     * @param infracoesValidadas
     * @return 
     * @throws RegraNegocioException 
     */
    public ProcessoAdministrativo validarInfracaoNoIntervalo1Ano(DadosInfracaoPAD infracao, 
                                                                        List<ProcessoAdministrativoInfracao> infracoesValidadas) throws RegraNegocioException {

        Date dataInicioIntervalo = Utils.addDayMonth(infracao.getDataInfracao(), -364);
        Date dataFimIntervalo = Utils.addDayMonth(infracao.getDataInfracao(), 364);

        for (ProcessoAdministrativoInfracao infracaoValidada : infracoesValidadas) {
            if (Utils.compareBetweenToDate(dataInicioIntervalo, dataFimIntervalo, infracaoValidada.getDataInfracao())) {
                return infracaoValidada.getProcessoAdministrativo();
            }
        }
        
        throw new RegraNegocioException("Não há infração no intervalo de 1ano.");
    }
    /**
     * @param infracao
     * @param infracoesValidadas
     * @throws RegraNegocioException 
     */
    public void validarInfracaoMesmoArtigoForaIntervalo(DadosInfracaoPAD infracao, List<ProcessoAdministrativoInfracao> infracoesValidadas) throws RegraNegocioException {

        Date dataInicioIntervalo = Utils.addDayMonth(infracao.getDataInfracao(), -364);
        Date dataFimIntervalo = Utils.addDayMonth(infracao.getDataInfracao(), 364);

        for (ProcessoAdministrativoInfracao infracaoValidada : infracoesValidadas) {
            if (Utils.compareBetweenToDate(dataInicioIntervalo, dataFimIntervalo, infracaoValidada.getDataInfracao())) {
                throw new RegraNegocioException("Não há infração com mesmo artigo para esta infração.");
            }
        }
    }
    
    /**
     * 
     * @param pasSuspensao
     * @param andamentosNaoPermitidos
     * @throws RegraNegocioException 
     */
    public void validarAndamentoPAReincidente(List<ProcessoAdministrativoWrapper> pasSuspensao, final List<Integer> andamentosNaoPermitidos) throws RegraNegocioException {
        Collection andamentos = Collections2.filter(pasSuspensao, new Predicate<ProcessoAdministrativoWrapper>() {
            @Override
            public boolean apply(ProcessoAdministrativoWrapper t) {
                return !andamentosNaoPermitidos.contains(t.getCodigoAndamento());
            }
        });
        
        if (DetranCollectionUtil.ehNuloOuVazio(andamentos)) {
            throw new RegraNegocioException("Andamento de PA reincidente não atende a regra.");
        }
    }
    
    /**
     * 
     * @param pasSuspensao
     * @throws RegraNegocioException 
     */
    public void validarAndamentoPAReincidenteCenarios(List<ProcessoAdministrativoWrapper> pasSuspensao) throws RegraNegocioException {

        List<Integer> andamentosNaoPermitidos = 
            DetranCollectionUtil
                .montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.AGUARDAR_GERACAO_NOTIFICACAO_ACOLHIMENTO,
                            PAAndamentoProcessoConstante.ARQUIVAMENTO.CONFIRMAR_ARQUIVAMENTO_PROCESSO,
                            PAAndamentoProcessoConstante.CANCELAMENTO.CONFIRMAR_CANCELAMENTO_PROCESSO,
                            PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_ARQUIVAMENTO_PENA_CUMPRIDA,
                            PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.CONFIRMAR_ARQUIVAMENTO_RECURSO_PROVIDO,
                            PAAndamentoProcessoConstante.APENSADO.CONFIRMAR_ARQUIVAMENTO_PROCESSO_AGRAVADO,
                            PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.AGUARDAR_AR_NOTIFICACAO_ACOLHIMENTO,
                            PAAndamentoProcessoConstante.EDITAL_NOTIFICACAO_ACOLHIMENTO.AGUARDAR_EDITAL_NOTIFICACAO_ACOLHIMENTO
                );
        
        validarAndamentoPAReincidente(pasSuspensao, andamentosNaoPermitidos);
    }

    /**
     * 
     * @param dadosCondutorPAD
     * @param infracao
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public DadosInfracaoPAD validaInfracaoPeriodoMaior4AnosEOutraInfracaoPeriodo1Ano(
        DadosCondutorPAD dadosCondutorPAD, DadosInfracaoPAD infracao) throws RegraNegocioException, AppException {
        
        Date dataIntervalo = Utils.addYear(Calendar.getInstance().getTime(), -4);
        DadosInfracaoPAD infracao4anos = null;

        //LOG.debug("Data Atual menos 4 anos: {0}", Utils.formatDate(dataIntervalo, "dd/MM/yyyy"));
        //LOG.debug("Data infração: {0}", Utils.formatDate(infracao.getDataInfracao(), "dd/MM/yyyy"));
        
        if (Utils.compareToDate(infracao.getDataInfracao(), dataIntervalo) < 0) {
            
            Date dataInfracaoMenos1Ano = Utils.addYear(infracao.getDataInfracao(), -1);
        
            Integer TAMANHO_EXTRATO = 14;
            
            String extrato 
                = DetranStringUtil.preencherEspaco(
                    infracao.getAuto().concat(infracao.getInfracaoCodigo()),
                    TAMANHO_EXTRATO,
                    DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO
                );
            
            AEMNPP88 aemnpp88
                = new AEMNPP88BO()
                    .executarIntegracaoAEMNPP88(
                        dadosCondutorPAD.getNomeCondutor(),
                        dataInfracaoMenos1Ano,
                        extrato,
                        infracao.getDataInfracao(),
                        dadosCondutorPAD.getCpf()
                    );
            
            Long ZERO = 0L;

            if (aemnpp88 != null 
                    && !DetranStringUtil.ehBrancoOuNulo(aemnpp88.getIsnReincidente()) 
                    && !Long.valueOf(aemnpp88.getIsnReincidente()).equals(ZERO)) {
                
                infracao4anos = new DadosInfracaoPAD(Long.parseLong(aemnpp88.getIsnReincidente()), 
                                                                      aemnpp88.getExtratoReinc(), 
                                                                      infracao.getInfracaoCodigo(), 
                                                                      Utils.getDate(aemnpp88.getDataInfracaoReinc(), "yyyyMMdd"), 
                                                                      Integer.parseInt(aemnpp88.getAutuadorReinc()), 
                                                                      Integer.parseInt(aemnpp88.getOrigemReinc()), 
                                                                      Integer.parseInt(aemnpp88.getQdePontosReinc()), 
                                                                      aemnpp88.getPlacaReinc(), 
                                                                      null, 
                                                                      null,
                                                                      "");
            }
        }
        return infracao4anos;
    }

    public List<DadosInfracaoPAD> validarInfracoes(EntityManager em, List<DadosInfracaoPAD> dadosInfracoes, Integer valorReferencia) throws AppException {
        List infracoesValidadas = new ArrayList<Object>();
        for (DadosInfracaoPAD infracao : dadosInfracoes) {
            try {
                validarRegraInfracaoCometidaAposPeriodoPermissionado(infracao);
                validarRegraInfracaoPrescrita(infracao, valorReferencia);
                infracoesValidadas.add(infracao);
            } catch (RegraNegocioException e) {

            }
        }
        return infracoesValidadas;
    }
    
      /**
     * @param em
     * @param infracao
     * @param wrapper
     * @param infracoesValidadas
     * @return
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public List<DadosInfracaoPAD> montaInfracoesParaPA(DadosInfracaoPAD infracao, InstaurarProcessoAdministrativoWrapper wrapper, List<DadosInfracaoPAD> infracoesValidadas) throws RegraNegocioException, AppException {

        List<DadosInfracaoPAD> infracoes = new ArrayList();
        
        validarRegraInfracaoPrescrita(infracao, wrapper.getValorReferenciaMes());
        infracoes.add(buscarInfracaoNoIntervalo(infracao, infracoesValidadas, wrapper));
        infracoes.add(infracao);
        return infracoes;
    }
    
        /**
     * @param wrapper
     * @return 
     */
    public Map<String, Collection<DadosInfracaoPAD>> gerarMapInfracoes(InstaurarProcessoAdministrativoWrapper wrapper) {

        List<DadosInfracaoPAD> teste = wrapper.getInfracoes();
        ImmutableListMultimap<String, DadosInfracaoPAD> listaMapeada
                = Multimaps.index(teste, new Function<DadosInfracaoPAD, String>() {
                    @Override
                    public String apply(DadosInfracaoPAD input) {
                        return input.getInfracaoCodigo();
                    }
                });
        ImmutableMap<String, Collection<DadosInfracaoPAD>> arvoreMapeada = listaMapeada.asMap();
        Predicate<Collection<DadosInfracaoPAD>> predicado = new Predicate<Collection<DadosInfracaoPAD>>() {
            @Override
            public boolean apply(Collection<DadosInfracaoPAD> t) {
                return t.size() > 1;
            }
        };
        Map<String, Collection<DadosInfracaoPAD>> mapInfracoes = Maps.filterValues(arvoreMapeada, predicado);
        return mapInfracoes;
    }
    
    
    /**
     * @param em
     * @param value
     * @param wrapper
     * @return
     * @throws AppException 
     */
    public List<DadosInfracaoPAD> filtrarInfracoesValidasRegrasGerais(EntityManager em, Collection<DadosInfracaoPAD> value, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {
        
        List<DadosInfracaoPAD> infracoes = new ArrayList();
        for (DadosInfracaoPAD item : value) {
            try {
                infracoes.addAll(validacoesGeraisInfracao(em, item, wrapper));
            } catch (RegraNegocioException e) {
                //LOG.warn("Infração não atende regras gerais: {0}", e.getMessage());
            }
        }
        return infracoes;
    }

    /**
     * @param infracao
     * @param infracoesValidadas
     * @param wrapper
     * @return
     * @throws RegraNegocioException 
     */
    private DadosInfracaoPAD buscarInfracaoNoIntervalo(DadosInfracaoPAD infracao, List<DadosInfracaoPAD> infracoesValidadas, InstaurarProcessoAdministrativoWrapper wrapper) throws RegraNegocioException, AppException {
        
        
        if(wrapper.getValorReferenciaIntervalo() == null) {
            DetranWebUtils.applicationMessageException("Parâmetro de apoio obrigatório inválido.[ValorReferenciaIntervalo]");
        }
        
        Date dataInicioIntervalo = Utils.addMonth(infracao.getDataInfracao(), wrapper.getValorReferenciaIntervalo()*(-1));
        Date dataFimIntervalo = Utils.addMonth(infracao.getDataInfracao(), wrapper.getValorReferenciaIntervalo());
        
        for (DadosInfracaoPAD infracaoValidada : infracoesValidadas) {
            if (!infracao.getId().equals(infracaoValidada.getId())
                    && infracao.getInfracaoCodigo().equals(infracaoValidada.getInfracaoCodigo())) {
                if (Utils.compareBetweenToDate(dataInicioIntervalo, dataFimIntervalo, infracaoValidada.getDataInfracao())) {
                    return infracaoValidada;
                }
            }
        }
        throw new RegraNegocioException("Não há infração histórico para esta infração.");
    }
    
    /**
     * @param em
     * @param infracao
     * @param wrapper
     * @return
     * @throws RegraNegocioException
     * @throws AppException 
     */
    private List<DadosInfracaoPAD> validacoesGeraisInfracao(EntityManager em, DadosInfracaoPAD infracao, InstaurarProcessoAdministrativoWrapper wrapper) throws RegraNegocioException, AppException {

        List<DadosInfracaoPAD> infracoesValidadas = new ArrayList<>();
        
        validarRegraInfracaoCometidaAposPeriodoPermissionado(infracao);

        validarCodigoAcaoInstauracao(em, infracao, wrapper);
        
        DadosInfracaoPAD infracao4anos = validaInfracaoPeriodoMaior4AnosEOutraInfracaoPeriodo1Ano(wrapper.getDadosCondutorPAD(), infracao);
        if(infracao4anos != null)
            infracoesValidadas.add(infracao4anos);
        infracoesValidadas.add(infracao);
        return infracoesValidadas;

    }
}
