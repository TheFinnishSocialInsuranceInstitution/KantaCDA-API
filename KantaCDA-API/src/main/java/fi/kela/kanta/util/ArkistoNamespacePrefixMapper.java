package fi.kela.kanta.util;

import java.util.HashMap;
import java.util.Map;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class ArkistoNamespacePrefixMapper extends NamespacePrefixMapper {

	private Map<String, String> namespaceMap = new HashMap<>();
	
	public ArkistoNamespacePrefixMapper() {
		namespaceMap.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
		namespaceMap.put("urn:hl7finland", "hl7fi");
	}
	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
		if (namespaceMap.containsKey(namespaceUri)) {
			return namespaceMap.get(namespaceUri);
		} else {
			return suggestion;
		}
	}

}
