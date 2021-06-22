package root.repositories;

import org.springframework.data.repository.CrudRepository;
import root.model.Tag;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Long> {

    /**
     * Метод загружает из базы все теги.
     *
     * @return список тегов.
     */
    List<Tag> findAll();

    /**
     * Метод проверяет имена всех тегов и ищет в них подстроку s.
     *
     * @param s строка запроса.
     * @return Список тегов, содержащих в имени подстроку s.
     */
    List<Tag> findAllByTitleStartingWith(String s);

    /**
     * Метод проверяет есть ли в базе тег с именем s. Если есть, возвращает 1. если нет, возвращает 0.
     *
     * @param s имя тега
     * @return количество тегов: 0 или 1.
     */
    int countAllByTitle(String s);

    /**
     * Метож возвращает тег с именем s.
     *
     * @param s имя тега.
     * @return один тег.
     */
    Tag findByTitle(String s);
}
