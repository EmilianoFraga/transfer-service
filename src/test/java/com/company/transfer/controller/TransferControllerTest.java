package com.company.transfer.controller;

import com.company.transfer.App;
import com.company.transfer.repositories.IAccountRepository;
import com.company.transfer.repositories.ITransferRepository;
import com.company.transfer.testutils.AmountConstants;
import java.nio.charset.Charset;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;


/**
 * Integration Tests (IT) for TransferController<br>
 * This test class requires Spring Boot and Mock MVC to run.<br>
 * Also an alternate property file must be used as in annotation below.<br>
 * The DirtiesContext annotation ensures that the Application Context is reset after each test method to ensure database cleanup.
 * 
 * @author emiliano
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        classes = App.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integration-tests.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransferControllerTest {

    private static MediaType contentType;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private ITransferRepository transferRepository;

    @BeforeClass
    public static void setupClass() throws Exception {
        contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                MediaType.APPLICATION_JSON.getSubtype(),
                Charset.forName("utf8"));
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        this.transferRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    public void testAddAccount() throws Exception {
        // first access to accounts must return an empty JSON Array
        this.mockMvc.perform(MockMvcRequestBuilders.get("/accounts/")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)))
                ;

        // insert one new account
        this.mockMvc.perform(MockMvcRequestBuilders.put("/accounts/new/")
                .param("name", "Test Account One")
                .param("initialBalance", AmountConstants.AMOUNT_100.toPlainString())
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Account One")))
                .andExpect(jsonPath("$.balance", is(100)))
                ;

        // after insert, query to accounts must return a JSON Array with created elements
        this.mockMvc.perform(MockMvcRequestBuilders.get("/accounts/")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Account One")))
                .andExpect(jsonPath("$[0].balance", is(100.0)))
                ;

        // insert another account
        this.mockMvc.perform(MockMvcRequestBuilders.put("/accounts/new/")
                .param("name", "Test Account Two")
                .param("initialBalance", AmountConstants.AMOUNT_50.toPlainString())
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Test Account Two")))
                .andExpect(jsonPath("$.balance", is(50)))
                ;

        // after insert, query to accounts must return a JSON Array with created elements
        this.mockMvc.perform(MockMvcRequestBuilders.get("/accounts/")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Account One")))
                .andExpect(jsonPath("$[0].balance", is(100.0)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Test Account Two")))
                .andExpect(jsonPath("$[1].balance", is(50.0)))
                ;
    }

    @Test
    public void testAddTransfer() throws Exception {
        // insert source account
        this.mockMvc.perform(MockMvcRequestBuilders.put("/accounts/new/")
                .param("name", "Source Account")
                .param("initialBalance", AmountConstants.AMOUNT_100.toPlainString())
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                ;

        // insert target account
        this.mockMvc.perform(MockMvcRequestBuilders.put("/accounts/new/")
                .param("name", "Target Account")
                .param("initialBalance", AmountConstants.AMOUNT_100.toPlainString())
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                ;

        // first access to transfers must return an empty JSON Array
        this.mockMvc.perform(MockMvcRequestBuilders.get("/transfers/")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)))
                ;


        // transfer between accounts
        this.mockMvc.perform(MockMvcRequestBuilders.put("/accounts/transfer/")
                .param("sourceId", "1")
                .param("destId", "2")
                .param("amount", AmountConstants.AMOUNT_10.toPlainString())
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        // after transfer, query to transfers must return a JSON Array with the new transfer
        this.mockMvc.perform(MockMvcRequestBuilders.get("/transfers/")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].sourceAccount.id", is(1)))
                .andExpect(jsonPath("$[0].destinationAccount.id", is(2)))
                .andExpect(jsonPath("$[0].amount", is(10.0)))
                ;

        // after transfer, query to accounts must return a JSON Array with updated elements
        this.mockMvc.perform(MockMvcRequestBuilders.get("/accounts/")
                .contentType(contentType))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Source Account")))
                .andExpect(jsonPath("$[0].balance", is(90.0)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Target Account")))
                .andExpect(jsonPath("$[1].balance", is(110.0)))
                ;
    }

    @Test (expected = NestedServletException.class)
    public void testAddAccounBlankName() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/accounts/new/")
                .param("name", "")
                .param("initialBalance", AmountConstants.AMOUNT_100.toPlainString())
                .contentType(contentType))
                ;
    }

    @Test (expected = NestedServletException.class)
    public void testAddAccounSameName() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/accounts/new/")
                .param("name", "Test Account One")
                .param("initialBalance", AmountConstants.AMOUNT_100.toPlainString())
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                ;

        this.mockMvc.perform(MockMvcRequestBuilders.put("/accounts/new/")
                .param("name", "Test Account One")
                .param("initialBalance", AmountConstants.AMOUNT_1000.toPlainString())
                .contentType(contentType))
                ;
    }

    @Test (expected = NestedServletException.class)
    public void testAddAccounNegativeBalance() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/accounts/new/")
                .param("name", "Test Account")
                .param("initialBalance", AmountConstants.AMOUNT_100.negate().toPlainString())
                .contentType(contentType))
                ;
    }
}
