package br.gov.ms.detran.processo.administrativo.bo;


import br.gov.ms.detran.processo.administrativo.core.bo.DadosCondutorPADBO;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author yanko.campos
 */
public class DadosCondutorPADBOTest {
    
    public DadosCondutorPADBOTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
//    @Test (expected = AppException.class)
    public void condutorPossuiCnhDefinitivaTest_ObjetoVazio() throws AppException {
        
        DadosCondutorPADBO instance = new DadosCondutorPADBO();
        
        DadosCondutorPAD dadosCondutor = new DadosCondutorPAD();
        
        instance.validaCnhDefinitiva(dadosCondutor);
        
    }
    
//    @Test (expected = AppException.class)
    public void condutorPossuiCnhDefinitivaTest_dataInvalida99999999() throws AppException {
        
        DadosCondutorPADBO instance = new DadosCondutorPADBO();
        
        DadosCondutorPAD dadosCondutor = new DadosCondutorPAD();
        
        dadosCondutor.setDataHabilitacaoDefinitiva(new Date(99999999));
        
        instance.validaCnhDefinitiva(dadosCondutor);
        
    }
    
//    @Test
    public void condutorPossuiCnhDefinitivaTest_dataValida() throws AppException {
        
        DadosCondutorPADBO instance = new DadosCondutorPADBO();
        
        DadosCondutorPAD dadosCondutor = new DadosCondutorPAD();
        
        dadosCondutor.setDataHabilitacaoDefinitiva(new Date(20180522));
        
        instance.validaCnhDefinitiva(dadosCondutor);
        
    }    
}