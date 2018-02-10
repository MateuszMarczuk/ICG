package pl.edu.wat.icg.common.file;

import pl.edu.wat.icg.common.base.BaseDto;

public abstract class FileDto extends BaseDto<Long> {
    private String name;

    public String getName() {
        return name;
    }

    private byte[] fileContent;

    public byte[] getFileContent() {
        return fileContent;
    }

    public FileDto(Long id, String name, byte[] fileContent) {
        super(id);
        this.name = name;
        this.fileContent = fileContent;
    }
}
