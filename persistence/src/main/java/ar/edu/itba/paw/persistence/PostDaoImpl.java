package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Repository
public class PostDaoImpl implements PostDao {

    // Constants with Table Names
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String MOVIES = TableNames.MOVIES.getTableName();
    private static final String POST_MOVIE = TableNames.POST_MOVIE.getTableName();
    private static final String TAGS = TableNames.TAGS.getTableName();
    private static final String COMMENTS = TableNames.COMMENTS.getTableName();

    /*
    *
    * Each SELECT, FROM and MAPPER static variable depend on each other.
    * If one is changed it is necessary to change the others as well to match the design decisions.
    * It is important that they are design only assuming the pre-existence and execution of
    * the BASE_POST static variables (the one Model linked with this Dao). That way, any other
    * can be made optional (currently the case of MOVIES and COMMENTS).
    * All functionality involving this constants is encapsulated in the FetchRelationSelector.
    * Use this enum to access Select, From and Mapper for each desired relation. In this enum the
    * relations are also categorized as required and not required.
    *
    * Additional requirements of each segment are made explicit below:
    *
    * - ALL:
    *   - All segments must abide to the aliases defined in BASE_POST_SELECT to access Post properties.
    *         Currently p_column_name.
    *
    *   - In BASE_POST_ROW_MAPPER a post_id to Post Map must be maintained for others to use.
    *
    *   - The use of LinkedHashSet, LinkedHashMap and List collections is of importance to maintain query order.
    *
    * - TAGS:
    *   - In BASE_POST_ROW_MAPPER, the tags Collection must be a Set to guaranty uniqueness.
    *
    * - MOVIES:
    *   - In BASE_POST_ROW_MAPPER, the movies Collection must be a Set to guaranty uniqueness.
    *
    */

    private enum FetchRelationSelector {
        POST(BASE_POST_SELECT, BASE_POST_FROM, true, null),
        TAGS(TAGS_SELECT, TAGS_FROM, true, null),
        MOVIES(MOVIES_SELECT, MOVIES_FROM, false, FetchRelation.MOVIES),
        COMMENTS(COMMENTS_SELECT, COMMENTS_FROM, false, FetchRelation.COMMENTS);

        private static final Map<EnumSet<FetchRelation>, ResultSetExtractor<Collection<Post>>>
                relationsToRSEMap = initializeRelationsToRSEMap();

        private final String select;
        private final String from;
        private final boolean required;
        private final FetchRelation relation;

        FetchRelationSelector(String select, String from, boolean required, FetchRelation relation) {
            this.select = select;
            this.from = from;
            this.required = required;
            this.relation = relation;
        }

        public static EnumSet<FetchRelationSelector> getSelectorsFromFetchRelations(EnumSet<FetchRelation> fetchRelations) {
            EnumSet<FetchRelationSelector> result = EnumSet.noneOf(FetchRelationSelector.class);

            for(FetchRelationSelector selector : FetchRelationSelector.values()) {
                if(selector.isRequired() || fetchRelations.contains(selector.getRelation()))
                    result.add(selector);
            }

            return result;
        }

        public static ResultSetExtractor<Collection<Post>> getMapper(EnumSet<FetchRelation> fetchRelations) {
            return relationsToRSEMap.get(fetchRelations);
        }

        public String getSelect() {
            return select;
        }

        public String getFrom() {
            return from;
        }

        public boolean isRequired() {
            return required;
        }

        private FetchRelation getRelation() {
            return relation;
        }

        private static Map<EnumSet<FetchRelation>, ResultSetExtractor<Collection<Post>>> initializeRelationsToRSEMap() {
            Map<EnumSet<FetchRelation>, ResultSetExtractor<Collection<Post>>> relationsToRSEMap = new HashMap<>();
            Set<EnumSet<FetchRelation>> relationsCombinations = new HashSet<>();

            // Add all FetchRelation combinations. TODO: Better way?
            relationsCombinations.add(EnumSet.noneOf(FetchRelation.class));
            relationsCombinations.add(EnumSet.of(FetchRelation.MOVIES));
            relationsCombinations.add(EnumSet.of(FetchRelation.COMMENTS));
            relationsCombinations.add(EnumSet.allOf(FetchRelation.class));

            for(EnumSet<FetchRelation> relationSet : relationsCombinations)
                relationsToRSEMap.put(relationSet, buildResultSetExtractor(relationSet));

            return relationsToRSEMap;
        }

        private static ResultSetExtractor<Collection<Post>> buildResultSetExtractor(EnumSet<FetchRelation> fetchRelations) {
            return (rs) -> {

                // Important use of LinkedHashMap to maintain Post insertion order
                final Map<Long, Post> idToPostMap = new LinkedHashMap<>();
                final Map<Long, Movie> idToMovieMap = new HashMap<>();
                final Map<Long, Comment> idToCommentMap = new HashMap<>();
                final Map<Long, Collection<Comment>> childrenWithoutParentMap = new HashMap<>();

                while(rs.next()) {

                    // Base Mapper Required
                    BASE_POST_ROW_MAPPER.accept(rs, idToPostMap);

                    // Tags Mapper Required - Business decision
                    TAGS_ROW_MAPPER.accept(rs, idToPostMap);

                    if (fetchRelations.contains(FetchRelation.MOVIES))
                        MOVIES_ROW_MAPPER.accept(rs, idToPostMap, idToMovieMap);

                    if (fetchRelations.contains(FetchRelation.COMMENTS))
                        COMMENTS_ROW_MAPPER.accept(rs, idToPostMap, idToCommentMap, childrenWithoutParentMap);
                }

                return idToPostMap.values();
            };
        }
    }


    // Mapper and Select for simple post retrieval.
    private static final String BASE_POST_SELECT = "SELECT " +
            // Posts Table Columns - Alias: p_column_name
            POSTS + ".post_id p_post_id, " +
            POSTS + ".creation_date p_creation_date, " +
            POSTS + ".title p_title, " +
            POSTS + ".body p_body, " + POSTS +
            ".word_count p_word_count, " +
            POSTS + ".email p_email";

    private static final String TAGS_SELECT = TAGS + ".tag p_tag";

    private static final String MOVIES_SELECT =
            MOVIES + ".movie_id m_movie_id, " +
            MOVIES + ".creation_date m_creation_date, " +
            MOVIES + ".title m_title, " +
            MOVIES + ".premier_date m_premier_date";

    private static final String COMMENTS_SELECT =
            COMMENTS + ".comment_id c_comment_id, " +
            "coalesce(" + COMMENTS + ".parent_id, 0) c_parent_id, " +
            COMMENTS + ".post_id c_post_id, " +
            COMMENTS + ".user_email c_user_email, " +
            COMMENTS + ".creation_date c_creation_date, " +
            COMMENTS + ".body c_body";

    private static final String BASE_POST_FROM = "FROM " + POSTS;

    private static final String TAGS_FROM =
            "LEFT OUTER JOIN " + TAGS + " ON " + POSTS + ".post_id = " + TAGS + ".post_id";

    private static final String MOVIES_FROM =
            "LEFT OUTER JOIN (" +
                    " SELECT " + MOVIES + ".movie_id, " + MOVIES + ".creation_date, " +
                    MOVIES + ".title, " + MOVIES + ".premier_date, " + "post_id" +
                    " FROM "+ POST_MOVIE +
                    " INNER JOIN " + MOVIES + " ON " + POST_MOVIE+ ".movie_id = " + MOVIES + ".movie_id" +
                    ") " + MOVIES + " on " + MOVIES + ".post_id = " + POSTS + ".post_id";

    private static final String COMMENTS_FROM =
            "LEFT OUTER JOIN " + COMMENTS + " ON " + POSTS + ".post_id = " + COMMENTS + ".post_id";


    private static final ResultSetMonoConsumer<Map<Long, Post>> BASE_POST_ROW_MAPPER = (rs, idToPostMap) -> {
        final long post_id = rs.getLong("p_post_id");

        if (!idToPostMap.containsKey(post_id)) {
            idToPostMap.put(post_id,
                    new Post(
                            post_id, rs.getObject("p_creation_date", LocalDateTime.class),
                            rs.getString("p_title"), rs.getString("p_body"),
                            rs.getInt("p_word_count"), rs.getString("p_email"),
                            new LinkedHashSet<>(), new LinkedHashSet<>(), new ArrayList<>()
                    )
            );
        }
    };

    private static final ResultSetMonoConsumer<Map<Long, Post>> TAGS_ROW_MAPPER = (rs, idToPostMap) -> {
        final long post_id = rs.getLong("p_post_id");
        final String tag = rs.getString("p_tag");

        if (tag != null)
            idToPostMap.get(post_id).getTags().add(tag);
    };

    private static final ResultSetBiConsumer<Map<Long, Post>, Map<Long, Movie>> MOVIES_ROW_MAPPER =
            (rs, idToPostMap, idToMovieMap) -> {

        final long post_id = rs.getLong("p_post_id");
        final long movie_id = rs.getLong("m_movie_id");

        // If movies is not null. (rs.getLong returns 0 on null)
        if(movie_id != 0) {

            if(!idToMovieMap.containsKey(movie_id)) {
                idToMovieMap.put(movie_id,
                        new Movie(
                                movie_id, rs.getObject("m_creation_date", LocalDateTime.class),
                                rs.getString("m_title"), rs.getObject("m_premier_date", LocalDate.class)
                        ));
            }

            // If the Post already had the Movie, it won't get added because the Collection is a Set
            idToPostMap.get(post_id).getMovies().add(idToMovieMap.get(movie_id));
        }
    };

    private static final ResultSetTriConsumer<Map<Long, Post>, Map<Long, Comment>, Map<Long, Collection<Comment>>> COMMENTS_ROW_MAPPER =
            (rs, idToPostMap, idToCommentMap, childrenWithoutParentMap) -> {

        final long comment_id = rs.getLong("c_comment_id");
        Comment newComment;

        // Returns 0 on null
        if(comment_id != 0 && !idToCommentMap.containsKey(comment_id)) {

            newComment = new Comment(comment_id,
                    rs.getObject("c_creation_date", LocalDateTime.class),
                    rs.getLong("c_post_id"), rs.getLong("c_parent_id"), new ArrayList<>(),
                    rs.getString("c_body"), rs.getString("c_user_email"));

            idToCommentMap.put(comment_id, newComment);

            // Incorporate all children that appeared before currentComment
            if(childrenWithoutParentMap.containsKey(comment_id)) {
                newComment.getChildren().addAll(childrenWithoutParentMap.get(comment_id));

                // Mapping is no longer necessary
                childrenWithoutParentMap.remove(comment_id);
            }

            // Comment is root
            if (newComment.getParentId() == 0)
                idToPostMap.get(newComment.getPostId()).getComments().add(newComment);

            else {
                // If parent doesn't exist yet
                if(!idToCommentMap.containsKey(newComment.getParentId())) {

                    // Initialize Collection inside Map if necessary
                    if(!childrenWithoutParentMap.containsKey(newComment.getParentId()))
                        childrenWithoutParentMap.put(newComment.getParentId(), new ArrayList<>());

                    // Add children to Parent Children Buffer
                    childrenWithoutParentMap.get(newComment.getParentId()).add(newComment);
                }

                // Parent exists -> Add to parent
                else
                    idToCommentMap.get(newComment.getParentId()).getChildren().add(newComment);
            }
        }
    };

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert postInsert;
    private final SimpleJdbcInsert postMoviesInsert;
    private final SimpleJdbcInsert tagsInsert;

    @Autowired
    public PostDaoImpl(final DataSource ds){

        jdbcTemplate = new JdbcTemplate(ds);

        postInsert = new SimpleJdbcInsert(ds)
                .withTableName(POSTS)
                .usingGeneratedKeyColumns("post_id");

        postMoviesInsert = new SimpleJdbcInsert(ds)
                .withTableName(POST_MOVIE);

        tagsInsert = new SimpleJdbcInsert(ds)
                .withTableName(TAGS);
    }


    @Override
    public Post register(String title, String email, String body, Collection<String> tags, Set<Long> movies) {

        body = body.trim();
        LocalDateTime creationDate = LocalDateTime.now();
        int wordCount = body.split("\\s+").length;

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("email", email);
        map.put("word_count", wordCount);
        map.put("body", body);
        map.put("tags", tags);

        final long postId = postInsert.executeAndReturnKey(map).longValue();

        for(Long movie_id: movies){
            map = new HashMap<>();
            map.put("movie_id", movie_id);
            map.put("post_id", postId);
            postMoviesInsert.execute(map);
        }

        for(String tag: tags){
            map = new HashMap<>();
            map.put("tag", tag);
            map.put("post_id", postId);
            tagsInsert.execute(map);
        }

        return new Post(postId, creationDate, title, body, wordCount, email, tags, Collections.emptyList(), Collections.emptyList());
    }

    // This method abstract the logic needed to perform select queries with or without movies.
    private Collection<Post> findPostsBy(String customWhereStatement, String[] orderByParams, Object[] args, EnumSet<FetchRelation> includedRelations){

        EnumSet<FetchRelationSelector> selectorSet = FetchRelationSelector.getSelectorsFromFetchRelations(includedRelations);

        final String select = selectorSet.stream()
                .reduce("", (ac, selector) -> ac + ", " + selector.getSelect(), String::concat)
                .substring(1); // Remove first ','

        final String from = selectorSet.stream()
                .reduce("", (ac, selector) -> ac + " " + selector.getFrom(), String::concat);

        final String orderByStatement = buildOrderByStatement(orderByParams);

        final String query = select + " " + from + " " + customWhereStatement + " " + orderByStatement;

        final ResultSetExtractor<Collection<Post>> rowMapper = FetchRelationSelector.getMapper(includedRelations);

        if(args != null)
            return jdbcTemplate.query(query, args, rowMapper);

        else
            return jdbcTemplate.query(query, rowMapper);
    }

    // TODO con que sobrecargas nos quedamos? Decidi que no hay una sin args. Ponele null.

    private Collection<Post> findPostsBy(String customWhereStatement, String orderByParam, Object[] args, EnumSet<FetchRelation> includedRelations){
        return findPostsBy(customWhereStatement, new String[]{ orderByParam }, args,  includedRelations);
    }

    private Collection<Post> findPostsBy(String[] orderByParams, EnumSet<FetchRelation> includedRelations){
        return findPostsBy("", orderByParams, null, includedRelations);
    }

    private Collection<Post> findPostsBy(String orderByParam, EnumSet<FetchRelation> includedRelations){
        return findPostsBy("", orderByParam, null, includedRelations);
    }

    private Collection<Post> findPostsBy(FilterCriteria[] filters, String[] orderByParams, Object[] args, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(buildWhereStatement(filters), orderByParams, args, includedRelations);
    }

    private Collection<Post> findPostsBy(FilterCriteria[] filters, String orderByParam, Object[] args, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(buildWhereStatement(filters), orderByParam, args, includedRelations);
    }

    // TODO discutir dar la posibilidad de elegir OR o AND
    private String buildWhereStatement(FilterCriteria[] filters) {
        return buildQueryStatement("WHERE", " OR ",
                Arrays.stream(filters).map(f -> f.filterQuery).toArray(String[]::new));
    }

    private String buildOrderByStatement(String[] columns) {
        return buildQueryStatement("ORDER BY", ", ", columns);
    }

    // Separator needs to come with white spaces included
    private String buildQueryStatement(String queryStart, String separator, String[] modifiers) {
        if(modifiers == null || modifiers.length == 0)
            return "";

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append(queryStart).append(' ');

        for(String modifier : modifiers){

            queryBuilder.append(modifier);
            queryBuilder.append(separator);
        }

        //Delete last separator
        return queryBuilder.substring(0, queryBuilder.length() - separator.length());
    }

    @Override
    public Optional<Post> findPostById(long id, EnumSet<FetchRelation> includedRelations){
        return findPostsBy(
                new FilterCriteria[]{ FilterCriteria.BY_POST_ID }, (String[]) null, new Object[]{ id }, includedRelations)
                .stream().findFirst();
    }

    @Override
    public Collection<Post> findPostsByTitleOrderByNewest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_POST_TITLE}, SortCriteria.NEWEST.query, new Object[] {title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByTitleOrderByOldest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_POST_TITLE}, SortCriteria.OLDEST.query, new Object[] {title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByMoviesOrderByNewest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_MOVIE_TITLE}, SortCriteria.NEWEST.query, new Object[] {title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByMoviesOrderByOldest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_MOVIE_TITLE}, SortCriteria.OLDEST.query, new Object[] {title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByTagsOrderByNewest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_TAGS}, SortCriteria.NEWEST.query, new Object[] {title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByTagsOrderByOldest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_TAGS}, SortCriteria.OLDEST.query, new Object[] {title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByTitleAndMoviesOrderByNewest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_POST_TITLE, FilterCriteria.BY_MOVIE_TITLE}, SortCriteria.NEWEST.query, new Object[] {title, title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByTitleAndMoviesOrderByOldest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_POST_TITLE, FilterCriteria.BY_MOVIE_TITLE}, SortCriteria.OLDEST.query, new Object[] {title, title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByTitleAndTagsOrderByNewest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_POST_TITLE, FilterCriteria.BY_TAGS}, SortCriteria.NEWEST.query, new Object[] {title, title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByTitleAndTagsOrderByOldest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_POST_TITLE, FilterCriteria.BY_TAGS}, SortCriteria.OLDEST.query, new Object[] {title, title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByTagsAndMoviesOrderByNewest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_TAGS, FilterCriteria.BY_MOVIE_TITLE}, SortCriteria.NEWEST.query, new Object[] {title, title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByTagsAndMoviesOrderByOldest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_TAGS, FilterCriteria.BY_MOVIE_TITLE}, SortCriteria.OLDEST.query, new Object[] {title, title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByTitleAndTagsAndMoviesOrderByNewest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_POST_TITLE, FilterCriteria.BY_MOVIE_TITLE, FilterCriteria.BY_TAGS}, SortCriteria.NEWEST.query, new Object[] {title, title, title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByTitleAndTagsAndMoviesOrderByOldest(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{FilterCriteria.BY_POST_TITLE, FilterCriteria.BY_MOVIE_TITLE, FilterCriteria.BY_TAGS}, SortCriteria.OLDEST.query, new Object[] {title, title, title}, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByMovieId(long movie_id, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{ FilterCriteria.BY_MOVIE_ID },
                POSTS + ".creation_date",
                new Object[] { movie_id }, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByMovieTitle(String movie_title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{ FilterCriteria.BY_MOVIE_TITLE },
                SortCriteria.NEWEST.query, new Object[]{ movie_title }, includedRelations);
    }

    @Override
    public Collection<Post> getAllPostsOrderByNewest(EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(SortCriteria.NEWEST.query, includedRelations);
    }

    @Override
    public Collection<Post> getAllPostsOrderByOldest(EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(SortCriteria.OLDEST.query, includedRelations);
    }

    @Override
    public Collection<Post> findPostsByPostAndMovieTitle(String title, EnumSet<FetchRelation> includedRelations) {
        return findPostsBy(new FilterCriteria[]{ FilterCriteria.BY_POST_TITLE, FilterCriteria.BY_MOVIE_TITLE },
                SortCriteria.NEWEST.query, new Object[]{ title, title }, includedRelations);
    }


    private enum FilterCriteria {
        BY_POST_ID(POSTS + ".post_id = ?"),

        BY_MOVIE_ID(
                POSTS + ".post_id IN ( " +
                "SELECT " + POST_MOVIE + ".post_id " +
                "FROM " + POST_MOVIE +
                " WHERE " + POST_MOVIE + ".movie_id = ?)"
        ),

        BY_POST_TITLE(POSTS + ".title ILIKE '%' || ? || '%'"),

        BY_TAGS(
                POSTS + ".post_id IN (" +
                " SELECT " + TAGS + ".post_id FROM " + TAGS +
                " WHERE " +  TAGS + ".tag ILIKE '%' || ? || '%' )"
        ),

        BY_MOVIE_TITLE(
                POSTS + ".post_id IN ( " +
                "SELECT " + POST_MOVIE + ".post_id " +
                " FROM " + POST_MOVIE +
                " INNER JOIN " + MOVIES + " ON " + POST_MOVIE + ".movie_id = " + MOVIES + ".movie_id " +
                " WHERE " + MOVIES + ".title ILIKE '%' || ? || '%')"
        );

        final String filterQuery;

        FilterCriteria(String filterQuery) {
            this.filterQuery = filterQuery;
        }
    }

//      TODO charlar beneficio de ENUM
      private enum SortCriteria {
        NEWEST(POSTS + ".creation_date desc"),
        OLDEST(POSTS + ".creation_date");
        //MOST_COMMENTS(),
        //MOST_LIKED(),

        final String query;

        SortCriteria(String query) {
            this.query = query;
        }
    }
}