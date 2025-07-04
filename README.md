# ImageMeta

ImageMeta is an Android app for viewing and editing EXIF metadata of images.  
It allows you to browse images, view all EXIF tags, and save edited metadata as a new copy.

## Features

- View all EXIF metadata of an image
- Edit EXIF tags (e.g., Artist, Description, GPS, etc.)
- Save changes as a new image in `Pictures/ImageMeta`
- Supports images from camera and gallery (editing only for writable images)
- Modern UI with RecyclerView and Material Design

## Screenshots

<table>
  <tr>
    <td><img src="SCREENSHOTS/1.png" width="300"/></td>
    <td><img src="SCREENSHOTS/2.png" width="300"/></td>
  </tr>
</table>

## How It Works

- Select an image from your device.
- View and edit EXIF metadata in a list.
- Save changes as a new image file (original remains unchanged).

## Requirements

- Android 7.0 (API 24) or higher
- Kotlin, AndroidX, Material Components

## Build & Run

1. Clone the repository:
   ```bash
   git clone https://github.com/jksalcedo/imagemeta-app.git
   ```
2. Open in Android Studio or IntelliJ IDEA.
3. Sync Gradle and build the project.
4. Run on a device or emulator.

## Notes

- Editing metadata is only supported for images your app can write to (e.g., not all gallery/cloud images).
- New images are saved in `Pictures/ImageMeta`.
