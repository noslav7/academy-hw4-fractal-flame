## Использование приложения

Ниже собраны все практические шаги: как собрать JAR, какие флаги есть у CLI, какие пресеты уже готовы и как быстро сделать собственный вариант. Документ полностью покрывает требования из `README.md`, но на понятном пошаговом языке.

### 1. Сборка JAR

```powershell
mvn clean package
```

Команда собирает «толстый» JAR с зависимостями по пути `target/project-1.0.jar`. Во всех примерах далее предполагается, что вы находитесь в корне проекта (`C:\workspace\t-academy\hw4-fractal-flame`).

### 2. Параметры командной строки

| Ключ | Что задаёт | Значение по умолчанию |
| --- | --- | --- |
| `-w`, `--width` | Ширина изображения (пиксели) | `1920` |
| `-h`, `--height` | Высота изображения | `1080` |
| `-i`, `--iteration-count` | Количество итераций Chaos Game | `2_500` |
| `-t`, `--threads` | Число потоков (≥1) | `1` |
| `--seed` | Seed генератора случайных чисел | `5.1234` |
| `-o`, `--output-path` | Путь к PNG-файлу | `result.png` |
| `-ap`, `--affine-params` | Глобальные аффинные коэффициенты (`a,b,c,d,e,f`) | `0.8,0,0,0,0.8,0` |
| `-f`, `--functions` | Список вариаций `название:вес,...` | встроенные по умолчанию |
| `-b`, `--burn-in` | Сколько итераций пропустить перед рисованием | `1_000` |
| `-br`, `--brightness` | Глобальная экспозиция | `1.0` |
| `-g`, `--gamma` | Гамма для тон-коррекции | `2.2` |
| `-gc`, `--gamma-correction` | Включить гамма-коррекцию | `false` |
| `-s`, `--symmetry-level` | Уровень симметрии (число копий) | `1` |
| `-c`, `--config` | Путь до JSON-конфига | нет |

Очередность переопределения параметров: **CLI > JSON > встроенные дефолты**. Быстрая проверка:

```powershell
java -jar target/project-1.0.jar --config config/presets/flame.json
```

### 3. Краткая шпаргалка по JSON

JSON-конфиг повторяет структуру из `README.md`. Основные блоки:

```json
{
  "size": { "width": 1920, "height": 1080 },
  "iteration_count": 1500000,
  "output_path": "output/flame.png",
  "threads": 4,
  "seed": 7.7,
  "burn_in": 20000,
  "gamma_correction": true,
  "gamma": 1.8,
  "symmetry_level": 4,
  "brightness": 2.2,
  "functions": [
    {
      "name": "pdj",
      "weight": 0.7,
      "params": { "a": 0.85, "b": 1.15, "c": 1.05, "d": 0.9 },
      "color": { "r": 0.2, "g": 0.95, "b": 0.6 },
      "color_index": 0.62,
      "affine": { "a": 0.35, "b": 0.42, "c": -0.28, "d": -0.34, "e": 0.37, "f": 0.18 }
    }
  ],
  "palette": { "colors": [ { "r": 0.1, "g": 0.1, "b": 0.2 }, ... ] },
  "camera": { "auto_fit": true, "fit_margin": 0.18, "fit_samples": 400000 },
  "affine_params": { "a": 0.85, "b": 0, "c": -0.05, "d": 0, "e": 0.85, "f": -0.05 }
}
```

Поддерживаются вариации (регистр не важен): `linear`, `swirl`, `horseshoe`, `spherical`, `sinusoidal`, `bubble`, `pdj`, `fan2`, `julian`. Для `pdj`, `fan2`, `julian` параметры берутся из блока `params`.

### 4. Каталог пресетов

Готовые конфиги лежат в `config/presets`. Каждый записывает результат в свою PNG внутри `output/`.

| Пресет | Команда | Что получается |
| --- | --- | --- |
| Flame Classic | `java -jar target/project-1.0.jar --config config/presets/flame.json` | Базовая сцена «flame» из задания (результат сохраняется в `output/flame.png`). |
| Nebula Bloom | `java -jar target/project-1.0.jar --config config/presets/nebula.json` | Плотные «газовые облака» с симметрией ×5 и вариациями `bubble/pdj/julian`. |
| Ember Storm | `java -jar target/project-1.0.jar --config config/presets/embers.json` | Тёплые «угли» на основе `horseshoe` и `fan2`, высокая контрастность. |
| Polar Aurora | `java -jar target/project-1.0.jar --config config/presets/aurora.json` | Квадратное изображение с яркими «ленточками» `sinusoidal/fan2`, хорошо подходит для постеров. |

### 5. Как собрать свой вариант

1. Скопируйте любой пресет в `config/presets/<название>.json`.
2. Увеличьте `iteration_count` (чем больше итераций, тем мягче градиенты). Для FullHD лучше стартовать от 800 000.
3. Настройте блок `functions`: поменяйте `name`, веса, добавьте `params`. В задании требуется минимум четыре разных вариации.
4. Подберите цвета:
   - `color` и `color_index` связывают функцию с конкретным участком палитры.
   - `brightness` регулирует общую экспозицию, `gamma` — контраст (меньше гамма → сочнее тени).
5. Запустите `java -jar target/project-1.0.jar --config config/presets/<название>.json`.

CLI-параметры можно комбинировать с JSON. Пример «разогрева» пресета:

```powershell
java -jar target/project-1.0.jar `
  --config config/presets/nebula.json `
  -t 8 `
  --brightness 2.8 `
  -f "bubble:1.0,fan2:0.8,julian:0.5"
```

Переопределения из командной строки действуют только на текущий запуск и не меняют файл.

### 6. Генерация нескольких изображений подряд

```powershell
$presets = @(
  "config/presets/nebula.json",
  "config/presets/embers.json",
  "config/presets/aurora.json"
)
foreach ($cfg in $presets) {
  java -jar target/project-1.0.jar --config $cfg
}
```

PowerShell последовательно прогонит список и сохранит PNG с уникальными именами.

### 7. Частые вопросы

- **“Unknown variation”** — убедитесь, что имя функции совпадает с одним из перечисленных выше.
- **Пустое/очень мелкое изображение** — увеличьте `camera.fit_samples` (от 200 000) и `iteration_count`.
- **Прозрачный фон** — итоговый `BufferedImage` заполняется чёрным цветом. Если видите «шахматку», проблема в вашем просмотрщике PNG.
- **Скорость** — выставляйте `-t` равным числу физических ядер и запускайте собранный JAR, а не IDE.
