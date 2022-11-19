/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.apo.UnidadePenalEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP21A;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP22;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP22A;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP22B;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP21BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP22BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoCnhRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusHistoricoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAStatusAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatusHistorico;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento240 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento240.class);

    private IBloqueioBCAService bloqueioService;

    public IBloqueioBCAService getBloqueioService() {

        if (bloqueioService == null) {
            bloqueioService = ServiceJndiLocator.<IBloqueioBCAService>lookup("ejb/BloqueioBCAService");
        }

        return bloqueioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 240.");

        ProcessoAdministrativo processoAdministrativo = wrapper.getProcessoAdministrativo();
        
        try {
            
            AEMNPP22 aemnpp22 = executaAEMNPP22(processoAdministrativo.getCpf());

            executaAEMNPP21(em, processoAdministrativo, aemnpp22);

            retornaProcessoAdministrativoParaAndamentoAnterior(em, processoAdministrativo);

        } catch (Exception e) {

            LOG.debug("Erro ao executar Bloqueio através da integração.", e);

            getFalhaService()
                .gravarFalhaProcessoAdministrativo(
                        e,
                        "Andamento 240 - Erro ao executar Bloqueio",
                        null,
                        processoAdministrativo.getNumeroProcesso()
                );
        }

        LOG.debug("Fim Andamento 240.");

        return null;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

    }
    
    /**
     * 
     * @param em
     * @param processo
     * @param aemnpp22
     * @return
     * @throws AppException 
     */
    public ParametroEnvioIntegracao montaParamsAEMNPP21InclusaoOuSubstituicao(EntityManager em, ProcessoAdministrativo processo, AEMNPP22 aemnpp22) throws AppException {
        
        ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();
        
        AEMNPP22A primeiroProcesso = DetranCollectionUtil.ehNuloOuVazio(aemnpp22.getDadosBloqueiosBaseLocal()) ? null : (AEMNPP22A) aemnpp22.getDadosBloqueiosBaseLocal().get(0);
        
        BloqueioBCA bloqueio = new BloqueioBCARepositorio().getBloqueioBcaPorPaEAtivo(em, processo.getId());
        
        PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, processo.getId());
        
        ProcessoAdministrativoInfracao processoAdministrativoInfracao = null;
        
        if(!TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(processo.getTipo()) && !TipoProcessoEnum.CASSACAO_JUDICIAL.equals(processo.getTipo())) {
        
            List<ProcessoAdministrativoInfracao> infracoes = new ProcessoAdministrativoInfracaoRepositorio().getInfracoesPorProcessoAdministrativoID(em, processo.getId());

            if(DetranCollectionUtil.ehNuloOuVazio(infracoes)) {
                DetranWebUtils.applicationMessageException("Não foi possível encontrar infração para o processo!");
            }
            
            processoAdministrativoInfracao = infracoes.get(0);
        }
        
        MovimentoCnh recolhimento = new MovimentoCnhRepositorio().getMovimentoPorProcessoAdministrativoEAcao(em, processo.getId(), AcaoEntregaCnhEnum.ENTREGA);
        
        params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.getCpf(), DetranTamanhoConstante.CPF, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.getTipo().getTipoProcessoMainframe(), 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(Utils.formatDate(bloqueio.getDataInclusao(), "yyyyMMdd"), 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(Utils.formatDate(penalidade.getDataInicioPenalidade(), "yyyyMMdd"), 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaCodigoRestricao(processo.getTipo()), 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        if(processoAdministrativoInfracao != null) {
            params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaArtigo(em, aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo(), processoAdministrativoInfracao.getId()), 26, DetranStringUtil.TipoDadoEnum.ALFA));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaInfracao(processoAdministrativoInfracao, aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo()), 4, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaNumeroAutoInfracao(processoAdministrativoInfracao, aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo()), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        } else {
            params.adicionarParametro(DetranStringUtil.preencherEspaco("", 26, DetranStringUtil.TipoDadoEnum.ALFA));
            params.adicionarParametro(DetranStringUtil.preencherEspaco("9999", 4, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco("", 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        }
        
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaCodigoBloqueio(processo.getTipo()), 1, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("MS", 2, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("12", 2, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.isJuridico() ? "1" : "2", 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recolhimento != null ? "1" : "2", 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaRequisitoCurso(processo, aemnpp22), 1, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaRequisitoExame(processo, aemnpp22), 1, DetranStringUtil.TipoDadoEnum.ALFA));
        
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaTipoPrazo(penalidade.getUnidadePenal()), 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(primeiroProcesso != null ? primeiroProcesso.getQtePenalidadeBloqueio() : penalidade.getValor().toString(), 5, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaQtdTotalPenalidade(aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo(), penalidade.getValor()), 5, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaNumeroRestricao(aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo()), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(montaDescricaoBloqueio(processo), 120, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("RES. 723/2018", 120, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("PROC."+processo.getNumeroProcesso().substring(4) + "/" +processo.getNumeroProcesso().substring(0,4), 50, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(montaCampoProcessos(em, processo.getCpf()), 1000, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 2, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 2, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 30, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 120, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(defineAtualizacaoBaseLocal(aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo()), 1, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(defineAtualizacaoBca(aemnpp22.getDadosBloqueiosBca(), processo.getTipo()), 1, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("S".equals(defineAtualizacaoBca(aemnpp22.getDadosBloqueiosBca(), processo.getTipo())) ? "S" : "N", 1, DetranStringUtil.TipoDadoEnum.ALFA));
        
        return params;
    }

    private String recuperaCodigoRestricao(TipoProcessoEnum tipo) {
        
        String codigoRestricao = "";
        
        if(TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)){
            codigoRestricao = "1";
        }
        
        if(TipoProcessoEnum.SUSPENSAO.equals(tipo)){
            codigoRestricao = "2";
        }
        
        if(TipoProcessoEnum.CASSACAO.equals(tipo) || TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo)){
            codigoRestricao = "3";
        }
        
        if(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(tipo)){
            codigoRestricao = "8";
        }
        
        return codigoRestricao;
    }

    private String recuperaArtigo(EntityManager em, List dadosBloqueioBaseLocal, TipoProcessoEnum tipo, Long idInfracao) throws DatabaseException {
        
        Object[] amparoLegal = (Object[]) new ProcessoAdministrativoInfracaoRepositorio().getAmparoLegalPorInfracaoId(em, idInfracao);
        
        String mbtArtigo = amparoLegal[2] == null ? "" : amparoLegal[2].toString();
        String mbtInciso = amparoLegal[4] == null ? "" : amparoLegal[4].toString();
        
        String artigo = mbtArtigo + mbtInciso;
        
        if(TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo) || TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)){
            artigo = "0";
        } else {
            if(!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueioBaseLocal)){
                for (Object bloqueio : dadosBloqueioBaseLocal) {
                    AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;

                    String motivo = recuperaMotivo(tipo);

                    if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                        artigo = aemnpp22a.getArtigo();

                        break;
                    }
                }
            }
            
        }
        
        return artigo;
    }

    private String recuperaInfracao(ProcessoAdministrativoInfracao infracao, List dadosBloqueioBaseLocal, TipoProcessoEnum tipo) {
        String infracaoRetorno = infracao.getCodigoInfracao().substring(0, 4);
        
        if(TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo) || TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)){
            infracaoRetorno = "9";
        } else {
            if(!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueioBaseLocal)){
                for(Object bloqueio : dadosBloqueioBaseLocal){
                    AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;

                    String motivo = recuperaMotivo(tipo);

                    if(motivo.equals(aemnpp22a.getCodigoBloqueio())){
                        infracaoRetorno = aemnpp22a.getInfracao();

                        break;
                    }
                }
            }
        }
        
        return infracaoRetorno;
    }

    private String recuperaNumeroAutoInfracao(ProcessoAdministrativoInfracao infracao, List dadosBloqueioBaseLocal, TipoProcessoEnum tipo) {
        String numeroAutoInfracao = infracao.getAutoInfracao();
        
        if(TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo)
                || TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)){
            numeroAutoInfracao = "";
        }
        
        if (TipoProcessoEnum.CASSACAO.equals(tipo)
                || TipoProcessoEnum.SUSPENSAO.equals(tipo)
                || TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(tipo)) {
            
            if(!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueioBaseLocal)){
                for (Object bloqueio : dadosBloqueioBaseLocal) {
                    AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;

                    String motivo = recuperaMotivo(tipo);

                    if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                        numeroAutoInfracao = aemnpp22a.getNumeroAutoInfracao();

                        break;
                    }
                }
            }
        }
        
        return numeroAutoInfracao;
    }

    private String recuperaCodigoBloqueio(TipoProcessoEnum tipo) {
        
        String codigoBloqueio = "";
        
        if(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(tipo)){
            codigoBloqueio = "A";
        }
        
        if(TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)){
            codigoBloqueio = "D";
        }
        
        if(TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo)){
            codigoBloqueio = "E";
        }
        
        return codigoBloqueio;
    }
    
    /**
     * 
     * @param aemnpp22
     * @return 
     */
    private String recuperaRequisitoCurso(ProcessoAdministrativo processo, AEMNPP22 aemnpp22) throws AppException {
        
        String requisitoCursoLocal  = getRequisitoCursoBaseLocal(aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo());
        String requisitoCursoBCA    = getRequisitoCursoBca(aemnpp22.getDadosBloqueiosBca(), processo.getTipo());
        
        if(!requisitoCursoLocal.equals(requisitoCursoBCA)){
            DetranWebUtils.applicationMessageException("Tratativa regra de negócio requisito curso inválida.");
        }
        
        return requisitoCursoLocal;
    }

    /**
     * 
     * @param aemnpp22
     * @return 
     */
    private String recuperaRequisitoExame(ProcessoAdministrativo processo, AEMNPP22 aemnpp22) throws AppException {
        
        String requisitoExameLocal  = getRequisitoExameBaseLocal(aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo());
        String requisitoExameBCA    = getRequisitoExameBca(aemnpp22.getDadosBloqueiosBca(), processo.getTipo());
        
        if(!requisitoExameLocal.equals(requisitoExameBCA)){
            DetranWebUtils.applicationMessageException("Tratativa regra de negócio requisito exame inválida.");
        }
        
        return requisitoExameLocal;
    }

    private String recuperaTipoPrazo(UnidadePenalEnum unidadePenal) {
        String tipoPrazo = "";
        
        if(UnidadePenalEnum.DIA.equals(unidadePenal)){
            tipoPrazo = "1";
        }
        
        if(UnidadePenalEnum.MES.equals(unidadePenal)){
            tipoPrazo = "2";
        }
        
        if(UnidadePenalEnum.ANO.equals(unidadePenal)){
            tipoPrazo = "3";
        }
        
        return tipoPrazo;
    }

    private String recuperaQtdTotalPenalidade(List dadosBloqueioBaseLocal, TipoProcessoEnum tipo, Integer valorPenalidade) {
        Integer qtdTotalPenalidade = valorPenalidade;
        
        if(!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueioBaseLocal)){
            for (Object bloqueio : dadosBloqueioBaseLocal) {
                AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;

                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                    qtdTotalPenalidade += Integer.parseInt(aemnpp22a.getQteTotalPenalidades());
                }
            }
        }
        
        return qtdTotalPenalidade.toString();
    }
    
    private String recuperaNumeroRestricao(List<Object> dadosBloqueioBaseLocal, TipoProcessoEnum tipo) {
        String numeroRestricao = "1";
        
        if(!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueioBaseLocal)){
            for (Object bloqueio : dadosBloqueioBaseLocal) {
                AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;

                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                    numeroRestricao = aemnpp22a.getNumeroRestricao();
                }
            }
        }
        
        return numeroRestricao;
    }

    private String montaDescricaoBloqueio(ProcessoAdministrativo processo) {
        String numeroPortaria = 
                processo.getNumeroPortaria() != null ?
                processo.getNumeroPortaria().substring(4) + " / " + processo.getNumeroPortaria().substring(0, 4) :
                "";
        
        String descricaoBloqueio = "PORTARIA PENALIZACAO " + numeroPortaria;
        
        return descricaoBloqueio;
    }

    private List<AEMNPP21A> recuperaProcessos(EntityManager em, String cpf) throws DatabaseException, AppException {
        List processosRetorno = new ArrayList();
        
        List<ProcessoAdministrativo> processos = new ProcessoAdministrativoRepositorio().getListProcessoAdministrativosAtivosPorCPFEntidadeCompleta(em, cpf);
        
        if(!DetranCollectionUtil.ehNuloOuVazio(processos) && processos.size() > 1){
            int n = processos.size() < 21 ? processos.size() : 21;
            
            for(int i = 1; i < n; i++){
                ProcessoAdministrativo processo = processos.get(i);
                
                AEMNPP21A aemnpp21a = new AEMNPP21A();
                
                aemnpp21a.setDescricaoProcesso(DetranStringUtil.preencherEspaco(montaDescricaoProcesso(em, processo), 50, DetranStringUtil.TipoDadoEnum.ALFA));
                
                processosRetorno.add(aemnpp21a);
            }
        } else {
            AEMNPP21A aemnpp21a = new AEMNPP21A();
            aemnpp21a.setDescricaoProcesso(DetranStringUtil.preencherEspaco(montaDescricaoProcesso(em, processos.get(0)), 50, DetranStringUtil.TipoDadoEnum.ALFA));
            processosRetorno.add(aemnpp21a);
        }
        
        return processosRetorno;
    }

    /**
     * 
     * @param em
     * @param processo
     * @return
     * @throws AppException 
     */
    private String montaDescricaoProcesso(EntityManager em, ProcessoAdministrativo processo) throws AppException {
        
        String descricaoProcesso;
        
        PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, processo.getId());
        
        String numeroProcesso = processo.getNumeroProcesso().substring(4) +"/"+ processo.getNumeroProcesso().substring(0,4);
        String unidadeMedida  = penalidade.getUnidadePenal() == null ? "" : penalidade.getUnidadePenal().name();
        
        if(TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(processo.getTipo()) || TipoProcessoEnum.CASSACAO_JUDICIAL.equals(processo.getTipo())) {
            
            descricaoProcesso = 
                    "PROC." 
                    + numeroProcesso 
                    + " " 
                    + penalidade.getValor().toString() 
                    + unidadeMedida;
            
        } else {
            
            List<NotificacaoComplemento> complementos
                    = new NotificacaoComplementoRepositorio()
                    .getListNotificacaoComplementoPorProcessoAdministrativo(em, processo.getId());

            if (DetranCollectionUtil.ehNuloOuVazio(complementos)) {
                DetranWebUtils.applicationMessageException("Não foi possível encontrar a portaria deste PA.");
            }

            String dataPortaria     = Utils.formatDate(complementos.get(0).getData(), "dd/MM/yyyy");
            String numeroPortaria   = complementos.get(0).getNumeroPortaria().substring(4) +"/"+ complementos.get(0).getNumeroPortaria().substring(0,4);

            descricaoProcesso = 
                    "PROC." 
                    + numeroProcesso 
                    + " PORT." 
                    + numeroPortaria 
                    + " DE " 
                    + dataPortaria 
                    + " " 
                    + penalidade.getValor().toString() 
                    + unidadeMedida;
        }
        
        return descricaoProcesso;
    }
    
    private String montaCampoProcessos(EntityManager em, String cpf) throws AppException{
        String campoProcessos = "";
        
        List<AEMNPP21A> processos = recuperaProcessos(em, cpf);
        
        for(AEMNPP21A aemnpp21a : processos){
            campoProcessos += aemnpp21a.getDescricaoProcesso();
        }
        
        return campoProcessos;
    }

    /**
     * 
     * @param dadosBloqueiosBaseLocal
     * @param tipo
     * @return 
     */
    private String getRequisitoExameBaseLocal(List<Object> dadosBloqueiosBaseLocal, TipoProcessoEnum tipo){
        
        String requisitoExame = "N";
        
        if(!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBaseLocal)){
            
            for(Object aemnpp22aObject : dadosBloqueiosBaseLocal){
                
                AEMNPP22A aemnpp22a = (AEMNPP22A) aemnpp22aObject;
                
                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                    requisitoExame = aemnpp22a.getRequisitoExame();
                    return requisitoExame;
                }
            }
        }
        
        return requisitoExame;
    }
    
    /**
     * 
     * @param dadosBloqueiosBaseLocal
     * @param tipo
     * @return 
     */
    private String getRequisitoCursoBaseLocal(List<Object> dadosBloqueiosBaseLocal, TipoProcessoEnum tipo){
        
        String requisitoCurso = "N";
        
        if(!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBaseLocal)){
            
            for(Object aemnpp22aObject : dadosBloqueiosBaseLocal){
                
                AEMNPP22A aemnpp22a = (AEMNPP22A) aemnpp22aObject;
                
                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                    requisitoCurso = aemnpp22a.getRequisitoCurso();
                    return requisitoCurso;
                }
            }
        }
        
        return requisitoCurso;
    }

    private String defineAtualizacaoBaseLocal(List<Object> dadosBloqueiosBaseLocal, TipoProcessoEnum tipo){
        
        String atualizacao = "I";
        
        if(!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBaseLocal)){
            
            for(Object bloqueio : dadosBloqueiosBaseLocal){
                
                AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;
                
                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                    atualizacao = "S";
                    break;
                }
            }
        }
        
        return atualizacao;
    }
    
    /**
     * 
     * @param dadosBloqueiosBca
     * @param tipo
     * @return 
     */
    private String getRequisitoExameBca(List<Object> dadosBloqueiosBca, TipoProcessoEnum tipo){
        
        String requisitoExame = "N";
        
        if(!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBca)){
            
            for(Object aemnpp22bObject : dadosBloqueiosBca) {
                
                AEMNPP22B aemnpp22b = (AEMNPP22B) aemnpp22bObject;
                
                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22b.getCodigoBloqueio())) {
                    requisitoExame = aemnpp22b.getRequisitoExame();
                    return requisitoExame;
                }
            }
        }
        
        return requisitoExame;
    }
    
    /**
     * 
     * @param dadosBloqueiosBca
     * @param tipo
     * @return 
     */
    private String getRequisitoCursoBca(List<Object> dadosBloqueiosBca, TipoProcessoEnum tipo){
        
        String requisitoCurso = "N";
        
        if(!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBca)){
            
            for(Object aemnpp22bObject : dadosBloqueiosBca) {
                
                AEMNPP22B aemnpp22b = (AEMNPP22B) aemnpp22bObject;
                
                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22b.getCodigoBloqueio())) {
                    requisitoCurso = aemnpp22b.getRequisitoCurso();
                    return requisitoCurso;
                }
            }
        }
        
        return requisitoCurso;
    }
    
    /**
     * 
     * @param dadosBloqueiosBca
     * @param tipo
     * @return 
     */
    private String defineAtualizacaoBca(List<Object> dadosBloqueiosBca, TipoProcessoEnum tipo){
        
        String atualizacao = "I";
        
        if(!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBca)){
            
            for(Object bloqueio : dadosBloqueiosBca){
                
                AEMNPP22B aemnpp22b = (AEMNPP22B) bloqueio;
                
                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22b.getCodigoBloqueio())) {
                    atualizacao = "S";
                    break;
                }
            }
        }
        
        return atualizacao;
    }
    
    private String recuperaMotivo(TipoProcessoEnum tipo) {
        String motivo = "";
        
        if(TipoProcessoEnum.SUSPENSAO.equals(tipo)){
            motivo = "2";
        }
        
        if(TipoProcessoEnum.CASSACAO.equals(tipo)){
            motivo = "3";
        }
        
        if(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(tipo)){
            motivo = "A";
        }
        
        if(TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)){
            motivo = "D";
        }
        
        if(TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo)){
            motivo = "E";
        }
        
        return motivo;
    }

    /**
     * 
     * @param em
     * @param processoAdministrativo 
     */
    private void retornaProcessoAdministrativoParaAndamentoAnterior(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        PAStatusAndamento paStatusAndamentoAtual 
            = new PAStatusAndamentoRepositorio()
                .getPAStatusAndamentoAtivoPorStatusEAndamento(em, ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018);
        
        PAOcorrenciaStatusHistorico paOcorrenciaStatusHistorico 
            = new PAOcorrenciaStatusHistoricoRepositorio().getUltimoHistoricoDiferentePAStatusAndamentoAtual(em, processoAdministrativo.getId(), paStatusAndamentoAtual.getId());

        PAStatusAndamento statusAndamento 
            = new PAStatusAndamentoRepositorio()
                .find(em, PAStatusAndamento.class, paOcorrenciaStatusHistorico.getIdStatusAndamento());

        PAFluxoProcesso fluxoProcesso 
            = new PAFluxoProcessoRepositorio()
                .find(em, PAFluxoProcesso.class, paOcorrenciaStatusHistorico.getIdFluxoProcesso());

        getProcessoAdministrativoService().alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(processoAdministrativo, fluxoProcesso.getCodigo(), statusAndamento.getAndamentoProcesso().getCodigo());
    }

    /**
     * 
     * @param cpf
     * @return 
     */
    private AEMNPP22 executaAEMNPP22(String cpf) throws AppException {
        
        AEMNPP22 aemnpp22 = new AEMNPP22BO().executarIntegracaoAEMNPP22(cpf);

        if(aemnpp22 == null
                || DetranCollectionUtil.ehNuloOuVazio(aemnpp22.getDadosBloqueiosBaseLocal())
                || DetranCollectionUtil.ehNuloOuVazio(aemnpp22.getDadosBloqueiosBca())) {

            DetranWebUtils.applicationMessageException("Nenhum bloqueio foi encontrado para o processo.");
        }
        
        return aemnpp22;
    }

    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @param aemnpp22 
     */
    private void executaAEMNPP21(EntityManager em, ProcessoAdministrativo processoAdministrativo, AEMNPP22 aemnpp22) throws AppException {
        
        ParametroEnvioIntegracao params = montaParamsAEMNPP21InclusaoOuSubstituicao(em, processoAdministrativo, aemnpp22);

        new AEMNPP21BO().executarIntegracaoAEMNPP21(params);
    }
}
