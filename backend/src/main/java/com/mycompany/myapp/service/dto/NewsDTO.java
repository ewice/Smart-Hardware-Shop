package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.News} entity.
 */
public class NewsDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NewsDTO)) {
            return false;
        }

        NewsDTO newsDTO = (NewsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, newsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NewsDTO{" +
            "id=" + getId() +
            ", image='" + getImage() + "'" +
            "}";
    }
}
