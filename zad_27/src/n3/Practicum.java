package n3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

enum SubtitleLanguage {
    ru, en, cn
}

class SubtitleItem {
    Map<SubtitleLanguage, String> values = new HashMap<>();
    LocalTime begin;
    LocalTime end;

    // Конструктор
    public SubtitleItem(Map<SubtitleLanguage, String> values, LocalTime begin, LocalTime end) {
        this.values = new HashMap<>(values);
        this.begin = begin;
        this.end = end;
    }

    public Map<SubtitleLanguage, String> getValues() {
        return new HashMap<>(values);
    }

    public void setValues(Map<SubtitleLanguage, String> values) {
        this.values = new HashMap<>(values);
    }

    public LocalTime getBegin() {
        return begin;
    }

    public void setBegin(LocalTime begin) {
        this.begin = begin;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubtitleItem that = (SubtitleItem) o;
        return Objects.equals(values, that.values) &&
                Objects.equals(begin, that.begin) &&
                Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, begin, end);
    }

    @Override
    public String toString() {
        return "SubtitleItem{" +
                "values=" + values +
                ", begin=" + begin +
                ", end=" + end +
                '}';
    }
}
class SubtitleListTypeToken extends TypeToken<List<SubtitleItem>> {
}

class LocalTimeTypeAdapter extends TypeAdapter<LocalTime> {

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public void write(JsonWriter out, LocalTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.format(timeFormatter));
        }
    }

    @Override
    public LocalTime read(JsonReader in) throws IOException {
        String timeStr = in.nextString();
        if (timeStr.contains(".") && timeStr.indexOf('.') == timeStr.length() - 2) {
            timeStr += "00"; // .1 → .100
        } else if (timeStr.contains(".") && timeStr.indexOf('.') == timeStr.length() - 3) {
            timeStr += "0";  // .12 → .120
        }
        return LocalTime.parse(timeStr, timeFormatter);
    }
}

public class Practicum {

    public static void main(String[] args) {
        List<SubtitleItem> subtitles = Arrays.asList(
                new SubtitleItem(Map.of(SubtitleLanguage.ru, "Здравствуйте!",
                        SubtitleLanguage.en, "Hello!",
                        SubtitleLanguage.cn, "Ni hao"),
                        LocalTime.of(0, 0, 15),
                        LocalTime.of(0, 0, 17)
                ),
                new SubtitleItem(Map.of(SubtitleLanguage.ru, "Привет!",
                        SubtitleLanguage.en, "Hi!",
                        SubtitleLanguage.cn, "Ni hao"),
                        LocalTime.of(0, 0, 21),
                        LocalTime.of(0, 0, 24)
                ),
                new SubtitleItem(Map.of(SubtitleLanguage.ru, "Как дела?",
                        SubtitleLanguage.en, "How are you?",
                        SubtitleLanguage.cn, "Ni hao ma"),
                        LocalTime.of(0, 0, 28),
                        LocalTime.of(0, 0, 31)
                ),
                new SubtitleItem(Map.of(SubtitleLanguage.ru, "Всё хорошо, спасибо!",
                        SubtitleLanguage.en, "I'm fine, thank you!",
                        SubtitleLanguage.cn, "Wo hen hao, xie xie"),
                        LocalTime.of(0, 0, 34),
                        LocalTime.of(0, 0, 37)
                )
        );

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .create();

        String subtitlesJson = gson.toJson(subtitles);
        System.out.println("Сериализованные субтитры:");
        System.out.println(subtitlesJson);

        List<SubtitleItem> parsed = gson.fromJson(subtitlesJson, new SubtitleListTypeToken().getType());

        if (parsed.equals(subtitles)) {
            System.out.println("\n✅ Субтитры десериализованы корректно.");
        } else {
            System.out.println("\n❌ Ошибка при десериализации.");
            System.out.println("Оригинал: " + subtitles);
            System.out.println("Результат: " + parsed);
        }
    }
}
