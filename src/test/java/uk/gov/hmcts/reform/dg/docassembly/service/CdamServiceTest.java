package uk.gov.hmcts.reform.dg.docassembly.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClientApi;
import uk.gov.hmcts.reform.ccd.document.am.model.Document;
import uk.gov.hmcts.reform.dg.docassembly.service.exception.DocumentTaskProcessingException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class CdamServiceTest {

    @InjectMocks
    private CdamService cdamService;

    @Mock
    private CaseDocumentClientApi caseDocumentClientApi;

    @Mock
    private ByteArrayResource byteArrayResource;

    private static final UUID docStoreUUID = UUID.randomUUID();

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void downloadFileCdam() throws Exception {

        Document document = Document.builder().originalDocumentName("template1.docx").build();
        File mockFile = new File("src/test/resources/template1.docx");
        InputStream inputStream = new FileInputStream(mockFile);

        Mockito.when(caseDocumentClientApi.getMetadataForDocument("xxx", "serviceAuth", docStoreUUID))
            .thenReturn(document);
        ResponseEntity responseEntity = ResponseEntity.accepted().body(byteArrayResource);
        Mockito.when(byteArrayResource.getInputStream()).thenReturn(inputStream);
        Mockito.when(caseDocumentClientApi.getDocumentBinary("xxx", "serviceAuth", docStoreUUID)).thenReturn(responseEntity);

        cdamService.downloadFile("xxx", "serviceAuth", docStoreUUID);

        Mockito.verify(caseDocumentClientApi, Mockito.atLeast(1)).getDocumentBinary("xxx", "serviceAuth", docStoreUUID);
        Mockito.verify(caseDocumentClientApi, Mockito.atLeast(1)).getMetadataForDocument("xxx", "serviceAuth", docStoreUUID);
    }

    @Test(expected = DocumentTaskProcessingException.class)
    public void downloadFileCdamNullResponseBody() throws Exception {

        ResponseEntity responseEntity = ResponseEntity.accepted().body(null);
        Mockito.when(caseDocumentClientApi.getDocumentBinary("xxx", "serviceAuth", docStoreUUID)).thenReturn(responseEntity);

        cdamService.downloadFile("xxx", "serviceAuth", docStoreUUID);

    }
}
