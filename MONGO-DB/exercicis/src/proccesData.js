// Funció per processar les dades i transformar-les a un format més adequat per MongoDB
function processYoutuberData(data) {
  const youtubers = Array.isArray(data.youtubers.youtuber) 
    ? data.youtubers.youtuber 
    : [data.youtubers.youtuber];
  
  return youtubers.map(youtuber => {
    // Assegurem que categories i videos siguin arrays
    const categories = Array.isArray(youtuber.categories.category) 
      ? youtuber.categories.category 
      : [youtuber.categories.category];
    
    const videos = Array.isArray(youtuber.videos.video) 
      ? youtuber.videos.video 
      : [youtuber.videos.video];
    
    // Convertim els videos a un format més adequat
    const processedVideos = videos.map(video => ({
      videoId: video.id,
      title: video.title,
      duration: video.duration,
      views: parseInt(video.views),
      uploadDate: new Date(video.uploadDate),
      likes: parseInt(video.likes),
      comments: parseInt(video.comments)
    }));
    
    // Retornem el document processat
    return {
      youtuberId: youtuber.id,
      channel: youtuber.channel,
      name: youtuber.n,
      subscribers: parseInt(youtuber.subscribers),
      joinDate: new Date(youtuber.joinDate),
      categories: categories,
      videos: processedVideos
    };
  });
}