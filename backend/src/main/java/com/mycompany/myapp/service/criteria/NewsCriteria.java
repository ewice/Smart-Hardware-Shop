package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.News} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.NewsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /news?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class NewsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter image;

    private Boolean distinct;

    public NewsCriteria() {}

    public NewsCriteria(NewsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.image = other.image == null ? null : other.image.copy();
        this.distinct = other.distinct;
    }

    @Override
    public NewsCriteria copy() {
        return new NewsCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getImage() {
        return image;
    }

    public StringFilter image() {
        if (image == null) {
            image = new StringFilter();
        }
        return image;
    }

    public void setImage(StringFilter image) {
        this.image = image;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NewsCriteria that = (NewsCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(image, that.image) && Objects.equals(distinct, that.distinct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, image, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NewsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (image != null ? "image=" + image + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
