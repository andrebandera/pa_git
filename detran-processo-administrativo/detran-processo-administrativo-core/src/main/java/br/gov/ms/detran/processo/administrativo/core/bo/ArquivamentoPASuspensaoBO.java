/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.ace.IAcessoService;
import br.gov.ms.detran.processo.administrativo.core.bo.recursoonline.RecursoOnlineBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.Movimentacao;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoMotivoEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.MovimentacaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoOnlineCanceladoWrapper;

import javax.persistence.EntityManager;
import java.util.List;

/**
 *
 * @author desenvolvimento
 */
public class ArquivamentoPASuspensaoBO {
    
    private static final Logger LOG = Logger.getLogger(ArquivamentoPASuspensaoBO.class);

    private IProcessoAdministrativoService service;
    
    private IAcessoService acessoService;

    private IPAControleFalhaService falhaService;
    
    public IProcessoAdministrativoService getService() {

        if (service == null) {
            service = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }

        return service;
    }

    public IAcessoService getAcessoService() {

        if (acessoService == null) {
            acessoService = (IAcessoService) JNDIUtil.lookup("ejb/AcessoService");
        }

        return acessoService;
    }

    public IPAControleFalhaService getFalhaService() {

        if (falhaService == null) {
            falhaService = (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }

        return falhaService;
    }
    
    public void executa(EntityManager em) throws AppException {

        Usuario usuarioDetran = (Usuario) getAcessoService().getUsuarioPorLogin("DETRAN");
        
        List<ProcessoAdministrativo> processos = new ProcessoAdministrativoRepositorio().getProcessosAdministrativosCassacaoComBloqueio(em);

        for (ProcessoAdministrativo processo : processos) {
            LOG.debug("Processo de Cassação: {0}", processo.getNumeroProcesso());

            try {
                
                getService().executaArquivamentoPASuspensaoPorProcessoCassacao(processo, usuarioDetran);

            } catch (Exception e) {
            
                LOG.debug("Erro ao executar processo cassado.", e);

                getFalhaService()
                        .gravarFalhaEspecifica(
                                processo.getCpf(),
                                "Falha ao executar arquivamento de Processos de SUSPENSAO para o PA de CASSACAO " + processo.getNumeroProcesso(),
                                "ARQUIVAMENTO_PROCESSOS_SUSP_POR_CASSACAO"
                        );
            }

        }
    }

    public void executaPorProcesso(EntityManager em, ProcessoAdministrativo processoCassacao, Usuario usuarioDetran) throws AppException {
        
        Boolean sucessoExecucao = Boolean.TRUE;

        List<ProcessoAdministrativo> paSuspensao = new ProcessoAdministrativoRepositorio().
                                                        getPASuspensaoSemPenalidadePorCPF(em, processoCassacao.getCpf());

        for (ProcessoAdministrativo processoSuspensao : paSuspensao) {

            try {
                LOG.debug("Processo de Suspensao: {0}", processoSuspensao.getNumeroProcesso());
                
                getService().arquivarProcessoPorCassacao(em, processoSuspensao, processoCassacao, usuarioDetran);
                
                new ExecucaoAndamentoManager().iniciaExecucao(new ExecucaoAndamentoEspecificoWrapper(processoSuspensao, 
                                                                                                     usuarioDetran == null ? null : usuarioDetran.getId(),
                                                                                                     null, null));

            } catch (Exception e) {
                sucessoExecucao = Boolean.FALSE;
                
                LOG.debug("Erro ao executar processo cassado.", e);

                getFalhaService()
                        .gravarFalhaEspecifica(
                                processoSuspensao.getCpf(),
                                "Falha ao executar arquivamento de Processo de suspensão " + processoSuspensao.getNumeroProcesso(),
                                "ARQUIVAMENTO_PROCESSO_SUSPENSAO"
                        );
            }
        }

        if (sucessoExecucao) {
            PAComplemento complemento = new PAComplemento(processoCassacao, PAParametroEnum.ARQUIVAMENTO_PROCESSADO, "1");
            new PAComplementoRepositorio().insert(em, complemento);
        }
    }

    public void arquivarProcesso(EntityManager em, ProcessoAdministrativo processoSuspensao, ProcessoAdministrativo processoCassacao, Usuario usuarioDetran) throws AppException{
        
        BloqueioBCA bloqueio = new BloqueioBCARepositorio().getBloqueioBCAporPaESituacaoEAtivo(em, processoSuspensao.getId(), SituacaoBloqueioBCAEnum.ATIVO);
       
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setProcessoAdministrativo(processoSuspensao);
        movimentacao.setObservacao(montarObservacao(processoCassacao));
        
        if(bloqueio == null){

            movimentacao.setMovimentacaoAcao(MovimentacaoAcaoEnum.ARQUIVAR);
            movimentacao.setMotivo(MovimentacaoMotivoEnum.INCLUSAO_BLOQUEIO_CASSACAO);

        } else {
            movimentacao.setMovimentacaoAcao(MovimentacaoAcaoEnum.ARQUIVAR_COM_BLOQUEIO);
            movimentacao.setMotivo(MovimentacaoMotivoEnum.INCLUSAO_BLOQUEIO_CASSACAO);
        }
        new MovimentacaoPABO().gravarMovimentacao(em,
                new MovimentacaoWrapper(movimentacao, processoSuspensao),
                usuarioDetran);

        new RecursoOnlineBO()
                .cancelarRecursoOnlineEmBackOffice(em,
                        processoSuspensao,
                        new RecursoOnlineCanceladoWrapper("Arquivamento do Processo.",
                                "",
                                "DETRAN"));
    }

    private String montarObservacao(ProcessoAdministrativo processoCassacao) {
        String obs;
        if(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(processoCassacao)){
            obs =  "Processo arquivado por haver processo de Cassação da PPD com bloqueio";
        }else{
            obs = "Processo arquivado por haver processo de Cassação com bloqueio";
        }
        return obs;
    }

}
