package pl.woelke.gus_api.soap.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class SoapXmlParser {

    public <T> T parse(String xmlResponse, Class<T> clazz) {
        try {
            // Inicjalizacja JAXBContext i unmarshallera
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // Przekształcenie XML na obiekt
            return clazz.cast(unmarshaller.unmarshal(new StringReader(xmlResponse)));
        } catch (JAXBException e) {
            throw new RuntimeException("Error during XML parsing", e);
        }
    }

}
