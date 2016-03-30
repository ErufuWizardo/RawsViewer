package erufu.wizardo.rawsviewer.db.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DIRECTORY_PREFERENCES")
public class DirectoryPreferences {

    @Id
    @Column(name = "directory_name", length = 256)
    private String directoryName;

    @Column(name = "current_file_name", nullable = false, length = 256)
    private String currentFileName;

    @Column(name = "scale_factor", nullable = false)
    private Double scaleFactor;

    @Column(name = "last_used", nullable = false)
    private LocalDateTime  lastUsed;
}
