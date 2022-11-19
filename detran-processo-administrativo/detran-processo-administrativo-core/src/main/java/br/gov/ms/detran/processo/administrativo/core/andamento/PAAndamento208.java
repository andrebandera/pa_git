package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoServicoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAApoioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoJsonRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PASituacaoJsonEnum;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoJson;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.PABPMSWrapper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;

public class PAAndamento208  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento208.class);
    
    private static final Integer TAMANHO_EXTRATO = 14;

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 208.");
        
        if (wrapper.getProcessoAdministrativo() == null) {
            DetranWebUtils.applicationMessageException("Processo Administrativo inválido.");
        }
        
        PAOcorrenciaStatus ocorrenciaAtual = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, wrapper.getProcessoAdministrativo().getId());
        
        if (ocorrenciaAtual == null || 
                !PAAndamentoProcessoConstante.PROCESSO_JURIDICO.GERAR_JSON_PJU
                        .equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
            
            DetranWebUtils.applicationMessageException("O andamento do Processo Administrativo é diferente do Andamento informado.");
        }

        List<ProcessoAdministrativoInfracao> infracoes
            = new ProcessoAdministrativoInfracaoRepositorio()
                .getExtratoEAutuadorECodigoInfracao(em, wrapper.getProcessoAdministrativo().getId());

        List<String> extratos   = new ArrayList();
        List<String> autuadores = new ArrayList();

        for (ProcessoAdministrativoInfracao infracao : infracoes) {

            /** Extratos. **/
            extratos.add(
                DetranStringUtil.preencherEspaco(
                    DetranStringUtil.preencherEspaco(infracao.getAutoInfracao(), 10, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO).concat(infracao.getCodigoInfracao()),
                    TAMANHO_EXTRATO,
                    DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO
                )
            );

            /** Autuadores. **/
            Integer autuador = new PAApoioRepositorio().getOrgaoAutuadorPeloID(em, infracao.getOrgaoAutuador());
            autuadores.add(autuador == null ? "" : autuador.toString());
        }

        PABPMSWrapper pabpmsWrapper = new PABPMSWrapper();

        /*** INÍCIO OBRIGATÓRIOS. ***/
        
        pabpmsWrapper.setIdProcessoAdministrativo(wrapper.getProcessoAdministrativo().getId());
        
        /** Dados Geral. **/
        pabpmsWrapper.setDadosGeral(new ProcessoAdministrativoServicoBO().geraDadosGeral(wrapper.getProcessoAdministrativo()));

        /** Dados Condutor. **/
        pabpmsWrapper.setDadosCondutor(new ProcessoAdministrativoServicoBO().geraRegistroCondutor(wrapper.getProcessoAdministrativo()));

        /** Dados PA. **/
        pabpmsWrapper
                .setDadosPA(
                        new ProcessoAdministrativoServicoBO().geraDadosPA(
                                em, 
                                wrapper.getProcessoAdministrativo(), 
                                pabpmsWrapper.getDadosCondutor(), 
                                pabpmsWrapper.getDadosGeral().getNumeroPortaria()));
        
        /*** FIM OBRIGATÓRIOS. ***/
        
        /** Dados Historico Pontuacao Nome. **/
        pabpmsWrapper.setDadosHistoricoPontuacaoNome(new ProcessoAdministrativoServicoBO().gerarHistoricoPontuacaoNome(wrapper.getProcessoAdministrativo()));

        /** Dados Infracao Local. **/
        pabpmsWrapper.setDadosInfracaoLocal(new ProcessoAdministrativoServicoBO().gerarDadosInfracaoLocal(wrapper.getProcessoAdministrativo(), extratos, autuadores));

        /** Histórico Pontuação. **/
        pabpmsWrapper.setDadosPontuacao(new ProcessoAdministrativoServicoBO().geraHistoricoPontuacao(wrapper.getProcessoAdministrativo(), pabpmsWrapper.getDadosCondutor()));
        
        new ProcessoAdministrativoServicoBO().executaAEMNPP98PorBlocos(pabpmsWrapper, wrapper.getProcessoAdministrativo(), extratos, autuadores);
        
        /** Detalhes Autuação Infração. **/
        pabpmsWrapper
            .setDadosDetalhesAutuacaoInfracao(new ProcessoAdministrativoServicoBO().geraDetalhesAutuacaoInfracaoWrapper(pabpmsWrapper.getDadosCondutor(), infracoes));

        /** Artigo Despacho. **/
        pabpmsWrapper.setArtigoDespacho(new ProcessoAdministrativoServicoBO().geraArtigoDespacho(em, wrapper.getProcessoAdministrativo(), pabpmsWrapper.getDadosCondutor(), null));
        
        /** Dados Juridicos (PJU) **/
        pabpmsWrapper.setDadosJuridicos(new ProcessoAdministrativoServicoBO().geraDadosJuridicos(em, wrapper.getProcessoAdministrativo()));

        /** Json. **/
        gravarArquivoJson(em, wrapper.getProcessoAdministrativo(), pabpmsWrapper);
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
    }
    
    /**
     * Verificar se já existe um arquivo Json gravado para o ProcessoAdministrativo. 
     * 
     * Se existir, definir data fim e ativo = 0.
     *
     * Gravar o arquivoJson com situacao = GERADO.
     *
     * @param em
     * @param processo
     * @param pabpmsWrapper
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException
     */
    public void gravarArquivoJson(EntityManager em, ProcessoAdministrativo processo, PABPMSWrapper pabpmsWrapper) throws DatabaseException, AppException {

        ProcessoAdministrativoJson paJsonExistente = 
                new ProcessoAdministrativoJsonRepositorio()
                        .getProcessoAdministrativoJsonPorProcessoAdministrativoAtivo(em, processo.getId());

        if (null != paJsonExistente) {

            paJsonExistente.setDataFim(Calendar.getInstance().getTime());
            paJsonExistente.setAtivo(AtivoEnum.DESATIVADO);

            new ProcessoAdministrativoJsonRepositorio().update(em, paJsonExistente);
        }

        ProcessoAdministrativoJson paJson = new ProcessoAdministrativoJson();
        paJson.setSituacao(PASituacaoJsonEnum.GERADO);
        paJson.setJson(DetranStringUtil.getInstance().toJson(pabpmsWrapper));
        paJson.setDataInicio(Calendar.getInstance().getTime());
        paJson.setProcessoAdministrativo(processo);
        paJson.setAtivo(AtivoEnum.ATIVO);
        
        new ProcessoAdministrativoJsonRepositorio().insert(em, paJson);
    }
}