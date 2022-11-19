package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.entidade.enums.inf.MotivoPenalidadeEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranDateUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.processo.administrativo.constantes.StatusAEMNPP89Constante;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP89BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.DadosInfracaoPADRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPessoaRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Lillydi
 */
public class RegrasGeralInstauracaoCondutorBO {

    /**
     * 
     * @param em
     * @param dadosCondutorPAD
     * @throws RegraNegocioException
     * @throws DatabaseException
     * @throws AppException 
     */
    public void regrasCondutorGeral(EntityManager em, DadosCondutorPAD dadosCondutorPAD) throws RegraNegocioException, DatabaseException, AppException {
        
        regraCNHDefinitiva(dadosCondutorPAD);
        
//        regraCnhCassada(dadosCondutorPAD.getCpf());
        
//        regraCondutorFalecido(em, dadosCondutorPAD);

        regraGeralCondutorComPACassacaoAtivo(em, dadosCondutorPAD);
        
    }

    /**
     *
     * @param dadosCondutorPAD
     * @throws RegraNegocioException, AppException
     */
    private void regraCNHDefinitiva(DadosCondutorPAD dadosCondutorPAD) throws RegraNegocioException {

        if (null == dadosCondutorPAD) {
            throw new RegraNegocioException("Dados do Condutor Inválidos.");
        }

        if (dadosCondutorPAD.getDataHabilitacaoDefinitiva() != null
                && (!DetranDateUtil.ehDataValida(dadosCondutorPAD.getDataHabilitacaoDefinitiva())
                        || dadosCondutorPAD.getDataHabilitacaoDefinitiva().after(Utils.convertDate("31122999", "ddMMyyyy")))) {
            throw new RegraNegocioException("Data Cnh Definitiva do Condutor Inválida.");
        }
    }

    /**
     *
     * @param em
     * @param dadosCondutorPAD
     * @throws RegraNegocioException
     * @throws DatabaseException
     */
    private void regraGeralCondutorComPACassacaoAtivo(EntityManager em, DadosCondutorPAD dadosCondutorPAD) throws RegraNegocioException, DatabaseException {

        if (null == dadosCondutorPAD) {
            throw new RegraNegocioException("Condutor Inválido.");
        }

        if (DetranStringUtil.ehBrancoOuNulo(dadosCondutorPAD.getCpf())) {
            throw new RegraNegocioException("Condutor Inválido.");
        }

        List<ProcessoAdministrativo> lProcessosAdministrativo
                = new ProcessoAdministrativoRepositorio()
                .getListProcessoAdministrativoAtivoCassacao(em, dadosCondutorPAD.getCpf());

        if (!DetranCollectionUtil.ehNuloOuVazio(lProcessosAdministrativo)) {
            throw new RegraNegocioException("Condutor não pode ser reincidente, existe Processo Administrativo.");
        }
    }

    /**
     * @param em
     * @param dadosCondutor
     * @throws br.gov.ms.detran.comum.util.exception.RegraNegocioException
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException
     */
    public void regraCondutorJaPossuiPAAtivoDeSuspensao(EntityManager em, DadosCondutorPAD dadosCondutor) throws RegraNegocioException, DatabaseException {

        if (dadosCondutor == null || DetranStringUtil.ehBrancoOuNulo(dadosCondutor.getCpf())) {
            throw new RegraNegocioException("Condutor inválido.");
        }

        List processosAdministrativos = 
                new ProcessoAdministrativoRepositorio()
                        .getProcessosAdministrativosSuspensaoAtivos(em, dadosCondutor.getCpf());

        if (DetranCollectionUtil.ehNuloOuVazio(processosAdministrativos)) {
            throw new RegraNegocioException("Não existe Processo Administrativo Ativo para o condutor.");
        }
    }
    
    /**
     * 
     * @param em
     * @param dadosCondutor
     * @return 
     * @throws RegraNegocioException
     * @throws DatabaseException 
     */
    public List<ProcessoAdministrativoWrapper> getPAAtivoDeSuspensaoPorPontuacao(EntityManager em, DadosCondutorPAD dadosCondutor) throws RegraNegocioException, DatabaseException {

        if (dadosCondutor == null || DetranStringUtil.ehBrancoOuNulo(dadosCondutor.getCpf())) {
            throw new RegraNegocioException("Condutor inválido.");
        }

        List processosAdministrativos = 
                new ProcessoAdministrativoRepositorio()
                        .getProcessosAdministrativosSuspensaoAtivosPorMotivo(em, dadosCondutor.getCpf(), MotivoPenalidadeEnum.QTD_PONTOS);

        if (DetranCollectionUtil.ehNuloOuVazio(processosAdministrativos)) {
            throw new RegraNegocioException("Não existe Processo Administrativo Ativo Por Pontuação para o condutor.");
        }
        return processosAdministrativos;
    }

    public void regraCondutorNaoPossuiPAAtivoDeSuspensaoPorEspecificada(EntityManager em, DadosCondutorPAD dadosCondutor) throws RegraNegocioException, DatabaseException {

        if (dadosCondutor == null || DetranStringUtil.ehBrancoOuNulo(dadosCondutor.getCpf())) {
            throw new RegraNegocioException("Condutor inválido.");
        }

        List processosAdministrativos = 
                new ProcessoAdministrativoRepositorio()
                        .getProcessosAdministrativosSuspensaoAtivosPorMotivo(em, dadosCondutor.getCpf(), MotivoPenalidadeEnum.INFRACOES_ESPECIFICADAS);

        if (!DetranCollectionUtil.ehNuloOuVazio(processosAdministrativos)) {
            throw new RegraNegocioException("Existe PA por Infrações Especificadas para este condutor.");
        }
    }
    
    /**
     * @param em
     * @param dadosCondutor
     * @throws br.gov.ms.detran.comum.util.exception.RegraNegocioException
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException
     */
    public void regraCondutorNaoPossuiPAAtivoDeSuspensao(EntityManager em, DadosCondutorPAD dadosCondutor) throws RegraNegocioException, DatabaseException {

        if (dadosCondutor == null || DetranStringUtil.ehBrancoOuNulo(dadosCondutor.getCpf())) {
            throw new RegraNegocioException("Condutor inválido.");
        }

        List processosAdministrativos = 
                new ProcessoAdministrativoRepositorio()
                        .getProcessosAdministrativosSuspensaoAtivos(em, dadosCondutor.getCpf());

        if (!DetranCollectionUtil.ehNuloOuVazio(processosAdministrativos)) {
            throw new RegraNegocioException("Já existe Processo Administrativo Ativo de Suspensão para o condutor.");
        }
    }
    
    /**
     * 
     * @param em
     * @param dadosCondutor
     * @return
     * @throws RegraNegocioException
     * @throws DatabaseException 
     */
    public List<ProcessoAdministrativoWrapper> getProcessosAdministrativosSuspensaoAtivosComAndamentoPorCPF(EntityManager em, DadosCondutorPAD dadosCondutor) throws RegraNegocioException, DatabaseException {

        if (dadosCondutor == null || DetranStringUtil.ehBrancoOuNulo(dadosCondutor.getCpf())) {
            throw new RegraNegocioException("Condutor inválido.");
        }

        List<ProcessoAdministrativoWrapper> pasSuspensao =  new ProcessoAdministrativoRepositorio()
            .getProcessosAdministrativosSuspensaoAtivosComAndamentoPorCPF(em, dadosCondutor.getCpf());
        
        
        if (DetranCollectionUtil.ehNuloOuVazio(pasSuspensao)) {
            throw new RegraNegocioException("Não existe PA de Suspensão Ativo para este condutor");
        }
        
        return pasSuspensao;
    }
    
    /**
     * @param cpf
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    private void regraCnhCassada(String cpf) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(cpf)) {
            throw new RegraNegocioException("Dados do Condutor Inválidos.");
        }

        if (!DetranStringUtil.ehBrancoOuNulo(new ConsultaCondutorMainframeBO().getMotivoCassacao(cpf))) {
            throw new RegraNegocioException("Condutor com CNH cassada.");
        }

    }

    /**
     * Se pessoa existir no nosso sistema, ela deve estar viva(!). Se estiver
     * morta, avisar MainFrame com STATUS = 'D'.
     *
     * @param em
     * @param dadosCondutorPAD
     * @throws br.gov.ms.detran.comum.util.exception.RegraNegocioException
     */
    private void regraCondutorFalecido(EntityManager em, DadosCondutorPAD dadosCondutorPAD) throws RegraNegocioException, AppException {
        
        if(dadosCondutorPAD == null || DetranStringUtil.ehBrancoOuNulo(dadosCondutorPAD.getCpf())) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        boolean isCondutorFalecido = new PAPessoaRepositorio().isCondutorFalecido(em, dadosCondutorPAD.getCpf());

        if (isCondutorFalecido) {

            List<DadosInfracaoPAD> listaInfracoes
                = new DadosInfracaoPADRepositorio().getTodasInfracoesPorCpfInfrator(em, dadosCondutorPAD.getCpf());

            if (!DetranCollectionUtil.ehNuloOuVazio(listaInfracoes)) {

                for (DadosInfracaoPAD infracaoPAD : listaInfracoes) {

                    new AEMNPP89BO()
                        .executarIntegracaoAEMNPP89(
                            infracaoPAD.getIsn(),
                            dadosCondutorPAD.getCpf(),
                            StatusAEMNPP89Constante.FALECIMENTO,
                            null
                        );
                }
            }

            throw new RegraNegocioException("Condutor falecido.");
        }
    }
}
