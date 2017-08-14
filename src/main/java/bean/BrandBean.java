package bean;

import java.util.List;

/**
 * @author holysky.zhao 2017/8/14 12:55
 * @version 1.0.0
 */
public class BrandBean {

    /**
     * brandId : 16380
     * name : 361
     * nameEn : 361
     * nameCn : null
     * parentId : null
     * aliasNames : []
     * logoUrl : null
     * brandParentId : 0
     * descEn : null
     * descCn : null
     * imageUrls : []
     * platforms : {"TM":{},"JUMEI":{},"JD":{}}
     * birthPlace : null
     * birthYear : null
     * fromUrl : null
     * active : true
     * created : 2017-08-11T09:53:40.261Z
     * creater : SYSTEM
     * modified : 2017-08-11T09:53:40.261Z
     * modifier : SYSTEM
     */

    private int brandId;
    private String name;
    private String nameEn;
    private String nameCn;
    private String parentId;
    private String logoUrl;
    private int brandParentId;
    private String descEn;
    private String descCn;
    private PlatformsBean platforms;
    private String birthPlace;
    private String birthYear;
    private String fromUrl;
    private boolean active;
    private String created;
    private String creater;
    private String modified;
    private String modifier;
    private List<?> aliasNames;
    private List<?> imageUrls;

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public int getBrandParentId() {
        return brandParentId;
    }

    public void setBrandParentId(int brandParentId) {
        this.brandParentId = brandParentId;
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEn(String descEn) {
        this.descEn = descEn;
    }

    public String getDescCn() {
        return descCn;
    }

    public void setDescCn(String descCn) {
        this.descCn = descCn;
    }

    public PlatformsBean getPlatforms() {
        return platforms;
    }

    public void setPlatforms(PlatformsBean platforms) {
        this.platforms = platforms;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getFromUrl() {
        return fromUrl;
    }

    public void setFromUrl(String fromUrl) {
        this.fromUrl = fromUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public List<?> getAliasNames() {
        return aliasNames;
    }

    public void setAliasNames(List<?> aliasNames) {
        this.aliasNames = aliasNames;
    }

    public List<?> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<?> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
