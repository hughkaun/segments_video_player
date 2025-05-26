class VideoSegment {
  final String path;
  final double start;
  final double end;

  VideoSegment(this.path, this.start, this.end);

  Map<String, dynamic> toMap() {
    return {
      'path': path,
      'start': start,
      'end': end,
    };
  }
}
