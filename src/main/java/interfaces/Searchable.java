package interfaces;

public interface Searchable {

    // checks if the object matches the given ID

    boolean matchesId(String id);

    // checks if the object matches the model

    boolean matchesModel(String model);

    // checks if the object matches the given status

    boolean matchesStatus(String status);

    // checks if the object matches the given make

    boolean matchesMake(String make);

    // general search across several fields

    boolean matchesSearchTerm(String searchTerm);
}