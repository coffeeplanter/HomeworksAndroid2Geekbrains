## Курсы Geekbrains по Android, 2-й уровень

### Занятие 2: хранение данных

### Домашнее задание

1. Написать приложение с двумя полями ввода(ключ-значение) и тремя кнопками: "Добавить", "Проверить" и "Удалить". При нажатии на "Добавить" сохраняет эти значения в SharedPreferences. "Проверить" проверяет по ключу значение и выводит во второе поле. "Удалить" удаляет по ключу.
2. Создать базу данных и хранить эти данные также и там.
3. Добавить фотографию и сохранить во внутренней и внешней памяти. Загрузить из места сохранения и посмотреть в приложении
4. Добавить шифрование при записи в SharedPreferences и расшифровку при загрузке из него.

### Занятие 3: поставщик контента

### Домашнее задание

1. Добавить к приложению с предыдущего урока контент-провайдер.
2. Сделать новое приложение-клиент, которое будет через провайдер получать и обновлять данные.

### Мой комментарий

Хорошее тренировочное задание, которое заставило попотеть.  
Я объединил два урока, т. к. они тесно связаны друг с другом. В одном проекта  два модуля: contentprovider и contentproviderclient — по сути отдельные приложения.  
Всё сделано по пунктам. Добавление фотографии реализовано через неявный интент к камере, при отображении снимка его размеры оптимизируются. Для шифрования использовалась библиотека javax.crypto.Cipher.

### Screenshot / Скриншот

![Screenshot](/Lesson_2.2_and_2.3/screenshot.png?raw=true "Screenshot")
