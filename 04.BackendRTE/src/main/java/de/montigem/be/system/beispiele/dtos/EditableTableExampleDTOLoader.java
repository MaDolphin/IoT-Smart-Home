/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.system.beispiele.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.rechnungsstellungstatus.RechnungsstellungStatus;
import de.montigem.be.util.DAOLib;

import java.util.Arrays;

public class EditableTableExampleDTOLoader extends EditableTableExampleDTOLoaderTOP {

  public EditableTableExampleDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    setDTO(createDTO());
  }

  public EditableTableExampleDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    setDTO(createDTO());
  }

  private EditableTableExampleDTO createDTO() {
    EditableTableExampleDTO dto = new EditableTableExampleDTO();

    EditableTableExampleSomeListEntryDTO s1 = new EditableTableExampleSomeListEntryDTO();
    s1.setId(0);
    s1.someEntry = RechnungsstellungStatus.PLANUNG;

    EditableTableExampleSomeListEntryDTO s2 = new EditableTableExampleSomeListEntryDTO();
    s2.setId(1);
    s2.someEntry = RechnungsstellungStatus.OFFENE_RECHNUNG;

    EditableTableExampleSomeListEntryDTO s3 = new EditableTableExampleSomeListEntryDTO();
    s3.setId(2);
    s3.someEntry = RechnungsstellungStatus.SAP;

    EditableTableExampleSomeListEntryDTO s4 = new EditableTableExampleSomeListEntryDTO();
    s4.setId(3);
    s4.someEntry = RechnungsstellungStatus.SAP_STORNIERT;

    EditableTableExampleEntryDTO entry1 = new EditableTableExampleEntryDTO();
    entry1.setId(0);
    entry1.entry1 = "Entry 1a";
    entry1.entry2 = "Entry 2a";
    entry1.someList = Arrays.asList(s1, s2);
    dto.entries.add(entry1);

    EditableTableExampleEntryDTO entry2 = new EditableTableExampleEntryDTO();
    entry2.setId(1);
    entry2.entry1 = "Entry 1b";
    entry2.entry2 = "Entry 2b";
    entry2.someList = Arrays.asList(s3, s4);
    dto.entries.add(entry2);

    return dto;
  }
}
