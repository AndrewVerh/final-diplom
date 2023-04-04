import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    private final Map<String, List<PageEntry>> dataBase = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы
        for (File pdf : pdfsDir.listFiles()) {
            var doc = new PdfDocument(new PdfReader(pdf));
            int pageNumber = doc.getNumberOfPages();
            for (int i = 1; i <= pageNumber; i++) {
                var page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> frequences = new HashMap<>(); // мапа<ключ, частота>
                for (var word : words) { // перебираем слова
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    frequences.put(word, frequences.getOrDefault(word, 0) + 1);
                }
                for (String word : frequences.keySet()) {
                    PageEntry pageEntry = new PageEntry(pdf.getName(), i, frequences.get(word));
                    if (dataBase.containsKey(word)) {
                        dataBase.get(word).add(pageEntry);
                    } else {
                        dataBase.put(word, new ArrayList<>());
                        dataBase.get(word).add(pageEntry);
                    }
                    List<PageEntry> result = dataBase.getOrDefault(word.toLowerCase(), Collections.emptyList());
                    Collections.sort(result);
                }

            }

        }
        // Сортировка страниц для каждого слова
        for (List<PageEntry> pages : dataBase.values()) {
            Collections.sort(pages);
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> result = dataBase.get(word);
        if (result != null) {
            return new ArrayList<>(result); // Возврат копии, предварительно отсортированных страниц
        } else {
            return new ArrayList<>();
        }
    }
}
