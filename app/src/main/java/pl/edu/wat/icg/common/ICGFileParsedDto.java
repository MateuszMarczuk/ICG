package pl.edu.wat.icg.common;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.edu.wat.icg.common.file.ICGFileDto;
import pl.edu.wat.icg.common.utils.FileParseUtils;

public class ICGFileParsedDto implements FileParsedDto {
    private String name;

    private Map<String,String> headers;
    private List<String> units;
    private Map<String, List<String>> rows;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat CREATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static ICGFileParsedDto fromICGFileDto(ICGFileDto fileDto) {
        if (fileDto==null || fileDto.getFileContent()==null)
            return null;

        String[] lines = new String(fileDto.getFileContent(), Charset.forName("ISO-8859-2")).split("\\r\\n|\\n|\\r", -1);
        Map<String,String> headers = FileParseUtils.parseHeaders(lines,0,1);
        List<String> parameters = FileParseUtils.parseLine(lines, 2);
        List<String> units = FileParseUtils.parseLine(lines, 3);
        List<List<String>> values = FileParseUtils.parseValues(lines, 4, lines.length - 1);

        Map<String, List<String>> rows = new LinkedHashMap<>() ;

        for (int j = 0; j< parameters.size(); j++) {
            rows.put(parameters.get(j), values.get(j));
        }
        return new ICGFileParsedDto(fileDto.getName(), headers, units, rows);
    }

    public ICGFileParsedDto(String name, Map<String, String> headers, List<String> units, Map<String, List<String>> rows) {
        this.name = name;
        this.headers = headers;
        this.units = units;
        this.rows = rows;
    }

    /**
     * Liczy średnią ze wszystkich wartości dla parametru
     * @param parameterName
     * @return
     *
     *
     *
     */
    public String getParameterValue(String parameterName){
        List<String> stringListOfValues = rows.get(parameterName);

        double sum = 0;
        double meanOfValues = 0;

        for(String value: stringListOfValues){
            String valueTmp = value.replace(",", ".");
            sum += Double.parseDouble(valueTmp);
        }
        if(stringListOfValues.size() > 0) {
            meanOfValues = sum / stringListOfValues.size();
        }
        return String.format("%.2f",meanOfValues);
    }

    public String getHeaderValue(String key) {
        return headers.get(key);
    }

    @Override
    public String getPatientName() {
        return getHeaderValue("FIRSTNAME");
    }

    @Override
    public String getPatientLastName() {
        return getHeaderValue("LASTNAME");
    }

    @Override
    public String getPatientPesel() {
        return getHeaderValue("CUSTOMERID");
    }

    @Override
    public Date getBirthDate(DateFormat dateFormat) throws ParseException {
        return dateFormat.parse(getHeaderValue("BIRTHDATE"));
    }

    @Override
    public Date getCreationDate(DateFormat dateFormat) throws ParseException {
        return dateFormat.parse(getHeaderValue("CREATIONDATE"));
    }

    public List<String> getParameterNames(){
        return new ArrayList<>(rows.keySet());
    }

    public boolean isValid() {
        return !headers.isEmpty() && !units.isEmpty() && !rows.isEmpty() && units.size()==rows.size() && getPatientName()!=null && getPatientLastName() !=null;
    }

    public Date getBirthDate() throws ParseException {
        return getBirthDate(DATE_FORMAT);
    }

    public Date getCreationDate() throws ParseException {
        return getCreationDate(CREATION_DATE_FORMAT);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, List<String>> getRows() {
        return rows;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
