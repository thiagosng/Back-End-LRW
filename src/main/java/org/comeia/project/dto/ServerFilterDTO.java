package org.comeia.project.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.comeia.project.search.SearchCriteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper=false)
public @Data class ServerFilterDTO implements Serializable {

	private String fullName;
	private String type;
	private String serverIp;
	
	public static List<SearchCriteria> buildCriteria(ServerFilterDTO filter) {
		List<SearchCriteria> criterias = new ArrayList<>();
		
		if(filter.getFullName() != null && !filter.getFullName().isEmpty()) { 
			criterias.add(new SearchCriteria("fullName", "%".concat(filter.getFullName()).concat("%")));
		}
		
		if(filter.getType() != null && !filter.getType().isEmpty()) { 
			criterias.add(new SearchCriteria("type", filter.getType()));
		}
		
		return criterias;
	}
}
