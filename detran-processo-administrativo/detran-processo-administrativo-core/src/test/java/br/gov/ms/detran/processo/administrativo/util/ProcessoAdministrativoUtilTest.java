/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.util;

import br.gov.ms.detran.comum.util.Utils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author desenvolvimento
 */
public class ProcessoAdministrativoUtilTest {

//    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoUtilTest.class);
    public ProcessoAdministrativoUtilTest() {
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

    /**
     * Test of getAnoAtual method, of class ProcessoAdministrativoUtil.
     */
//    @Test
    public void testGetAnoAtual() {

//        LOG.info("ProcessoAdministrativoUtil.getAnoAtual");
        ProcessoAdministrativoUtil instance = new ProcessoAdministrativoUtil();

        Integer expResult = Utils.getAnoCorrente();

        Integer result = instance.getAnoAtual();

        assertEquals(expResult, result);
    }
}
