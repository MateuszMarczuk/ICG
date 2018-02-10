package pl.edu.wat.icg.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FileParsedDto {
    boolean isValid();

    Date getBirthDate() throws ParseException;

    Date getCreationDate() throws ParseException;

    Map<String, String> getHeaders();

    Map<String, List<String>> getRows();

    String getName();

    void setName(String name);

    String getHeaderValue(String key);

     String getPatientName();

     String getPatientLastName();

     String getPatientPesel();

     Date getBirthDate(DateFormat dateFormat) throws ParseException;

     Date getCreationDate(DateFormat dateFormat) throws ParseException;
}
